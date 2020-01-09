package com.ping.student.controller;

import com.alibaba.fastjson.JSONObject;
import com.ping.student.pojo.Attachment;
import com.ping.student.pojo.Student;
import com.ping.student.service.AttachmentService;
import com.ping.student.service.StudentService;
import com.ping.student.utils.UploadPic;
import com.ping.student.utils.WordUtil;
import jxl.Sheet;
import jxl.Workbook;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;


@Controller
@RequestMapping("/student")
public class StudentController {

    @Value("${attachment-path}")
    private  String attachment_path;

    @Value("${word-path}")
    private  String word_path;

    @Autowired
    private StudentService studentService;

    @Autowired
    private AttachmentService attachmentService;

    /**
     * 新增学生
     * @return
     */
    @ResponseBody
    @RequestMapping("/addStu")
    public String addStu(@RequestBody Student student){
        studentService.addStu(student);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("isOk","ok");
        return jsonObject.toString();
    }

    /**
     * 编辑学生
     * @return
     */
    @ResponseBody
    @RequestMapping("/editStu")
    public String editStu(@RequestParam String id){
        Student student = studentService.getOne(id);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("isOk","ok");
        jsonObject.put("student",student);
        return jsonObject.toString();
    }
    /**
     * 修改学生
     * @return
     */
    @ResponseBody
    @RequestMapping("/updateStu")
    public String updateStu(@RequestBody Student student){
        studentService.updateStu(student);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("isOk","ok");
        return jsonObject.toString();
    }

    /**
     * 删除学生
     * @return
     */
    @ResponseBody
    @RequestMapping("/delStu")
    public String delStu(@RequestParam String id){
        studentService.delStu(id);
        Attachment a = attachmentService.getAttachment(id);
        if(a != null){
            attachmentService.delAttachment(a.getId());
            String fileName = attachment_path+a.getFilepath();
            UploadPic.delFile(fileName);
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("isOk","ok");
        return jsonObject.toString();
    }

    /**
     * 获取学生列表
     * @return
     */
    @ResponseBody
    @RequestMapping("/getStuList")
    public String getStuList(@RequestBody LinkedHashMap<String,String> student){
        Map<String,Object> params = new HashMap<>();
        params.put("name",student.get("name"));
        params.put("clazz",student.get("clazz"));
        params.put("sex",student.get("sex"));
        int count = studentService.getAllCount(params);
        int pageSize = 10;
        int currentPage = Integer.valueOf(student.get("currentPage"));
        String start = ((currentPage-1)*pageSize+1)+"";
        String end = (currentPage*pageSize <= count ? currentPage*pageSize : count)+"";
        params.put("start",start);
        params.put("end",end);
        List<Student> list = studentService.getAll(params);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("isOk","ok");
        jsonObject.put("list",list);
        jsonObject.put("student",student);
        jsonObject.put("total",count);
        jsonObject.put("pageSize",pageSize);
        jsonObject.put("currentPage",currentPage);
        return jsonObject.toString();
    }

    //目前，此方法只支持上传xls文件的导入。
    @ResponseBody
    @RequestMapping("/importExcel")
    public String importExcel(@RequestParam MultipartFile file){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("isOk","ok");
        try {
            String fileName = file.getOriginalFilename();
            int indexdot =  fileName.indexOf(".");
            String suffix =  fileName.substring(indexdot+1);
            if(!suffix.equals("xls")&&!suffix.equals("xlsx")){
                jsonObject.put("isOk","unsuit");
                return jsonObject.toString();
            }
            InputStream in = file.getInputStream();
            Workbook wb = Workbook.getWorkbook(in);
            Sheet sheet = wb.getSheet(0); // 默认取第一个Sheet
            int totalCount = sheet.getRows();// 总行数
            int columns = sheet.getColumns(); // 总列数
            String[] arr = {"姓名","年龄","班级","性别"};
            boolean flag = true;
            for (int m = 0; m < columns; m++)
            {
                String cname = sheet.getCell(m, 0).getContents().trim();
                if(!cname.equals(arr[m])){
                    flag = false;
                    jsonObject.put("isOk","formatError");
                    break;
                }
            }
            if(flag){
                if (1 < totalCount)
                {
                    // 遍历文档的每一行，提取数据.
                    for (int i = 1; i < totalCount; i++)
                    {
                        String[] arr0 = new String[4];
                        for (int j = 0; j < columns; j++){
                            arr0[j] = sheet.getCell(j, i).getContents().trim().equals("") ? "''" : sheet.getCell(j, i).getContents().trim();
                        }
                        Student student = new Student();
                        student.setId(UUID.randomUUID().toString());
                        student.setName(arr0[0]);
                        student.setAge(arr0[1]);
                        student.setClazz(arr0[2]);
                        student.setSex(arr0[3]);
                        studentService.addStu(student);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            jsonObject.put("isOk","no");
        }finally {
            return jsonObject.toString();
        }
    }

    // excel导出。
    @RequestMapping("/exportExcel")
    public void exportExcel(@RequestParam String name, @RequestParam String clazz,@RequestParam String sex,HttpServletResponse response){
        Map<String,Object> params = new HashMap<>();
        params.put("name",name);
        params.put("clazz",clazz);
        params.put("sex",sex);
        List<Student> list = studentService.getAllStu(params);

        HSSFWorkbook workbook = new HSSFWorkbook();
        /** ***************** 样式定义开始 ****************** */
        // 定义全局文本样式，也可为某行某列单独定义
        HSSFCellStyle style_title = workbook.createCellStyle();
        HSSFCellStyle style = workbook.createCellStyle();
        HSSFCellStyle style1 = workbook.createCellStyle();

        HSSFFont font_title = workbook.createFont();
        font_title.setFontName("方正小标宋_GBK");// 设置字体
        font_title.setFontHeightInPoints((short) 24);// 设置字体大小
        style_title.setFont(font_title);
        style_title.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 设置垂直居中
        style_title.setWrapText(true);
        style_title.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 设置文本居中

        // 字体设置
        HSSFFont font = workbook.createFont();
        font.setFontName("宋体");// 设置字体
        font.setFontHeightInPoints((short) 11);// 设置字体大小
        style.setFont(font);
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 设置垂直居中
        style.setWrapText(true);
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 设置文本居中
        style.setBorderBottom((short) 1);
        style.setBorderLeft((short) 1);
        style.setBorderTop((short) 1);
        style.setBorderRight((short) 1);

        HSSFFont font1 = workbook.createFont();
        font1.setFontName("黑体");// 设置字体
        font1.setFontHeightInPoints((short) 11);// 设置字体大小
        style1.setFont(font1);
        style1.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 设置垂直居中
        style1.setWrapText(true);
        style1.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 设置文本居中
        style1.setBorderBottom((short) 1);
        style1.setBorderLeft((short) 1);
        style1.setBorderTop((short) 1);
        style1.setBorderRight((short) 1);
        /** ***************** 样式定义结束 ****************** */
        HSSFSheet sheet = workbook.createSheet();// 建工作表
        workbook.setSheetName(0, "学生入学注册信息表");// 设置表名
        sheet.setColumnWidth(0, 30 * 100);
        sheet.setColumnWidth(1, 30 * 100);
        sheet.setColumnWidth(2, 30 * 100);
        sheet.setColumnWidth(3, 30 * 100);

        // 添加标题行
        HSSFRow row_title = sheet.createRow(0);
        row_title.setHeight((short) (48 * 15));
        HSSFCell cell_title = row_title.createCell((short) 0);
        cell_title.setCellType(HSSFCell.CELL_TYPE_STRING);
        cell_title.setCellValue("学生入学注册信息表");
        cell_title.setCellStyle(style_title);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3)); // 合并单元格显示

        // 统计数据展现
        String column = "";
        String[] columnArr = {"姓名","年龄","班级","性别"};
        HSSFRow row_column = sheet.createRow(1);
        row_column.setHeight((short) (30 * 20));
        for (int i = 0; i < columnArr.length; i++)
        {
            HSSFCell cell_column = row_column.createCell((short) i);
            cell_column.setCellType(HSSFCell.CELL_TYPE_STRING);
            cell_column.setCellValue(columnArr[i]);
            cell_column.setCellStyle(style);
        }

        for (int i = 0; i < list.size(); i++)
        {
            Student s = list.get(i);
            String[] cell = new String[4];
            cell[0] = s.getName();
            cell[1] = s.getAge();
            cell[2] = s.getClazz();
            cell[3] = s.getSex();

            HSSFRow row_column1 = sheet.createRow(i + 2);
            row_column1.setHeight((short) (33 * 20));
            for (int x = 0; x < cell.length; x++)
            {
                HSSFCell cell_column = row_column1.createCell((short) x);
                cell_column.setCellType(HSSFCell.CELL_TYPE_STRING);
                cell_column.setCellValue(cell[x]);
                cell_column.setCellStyle(style1);
            }
        }
        response.reset();
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        try
        {
            response.setHeader("Content-Disposition",
                    "attachment;filename=" + URLEncoder.encode("学生入学注册信息表.xls", "UTF-8"));
            workbook.write(response.getOutputStream());
            response.getOutputStream().flush();
            response.getOutputStream().close();
        } catch (UnsupportedEncodingException e1)
        {
            e1.printStackTrace();
        } catch (IOException e1)
        {
            e1.printStackTrace();
        }
    }


    /**
     * 编辑学生
     * @return
     */
    @ResponseBody
    @RequestMapping("/getStu")
    public String getStu(@RequestParam String stuid){
        Student student = studentService.getOne(stuid);
        Attachment attachment = attachmentService.getAttachment(student.getId());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("isOk","ok");
        jsonObject.put("student",student);
        if(attachment == null){
            jsonObject.put("attachment","none");
        }else{
            jsonObject.put("attachment",attachment);
        }
        return jsonObject.toString();
    }


    /**
     * 上传图片
     * 拼接路径时注意/，少了会找不到。
     * @param uploadFile
     * @param req
     * @return
     * @throws IOException
     */
    @ResponseBody
    @RequestMapping("/upload")
    public String uploadImg(@RequestParam("upload") MultipartFile uploadFile,HttpServletRequest req,@RequestParam("stuid") String stuid) throws IOException {
        JSONObject json= new JSONObject();
        // 1.先获取服务器上存储图片的文件夹路径
        String realPath = attachment_path;//
        // 在本地磁盘存储文件。(转成软编码，若系统不一致，则可以在xml文件中修改。)
        /*String realPath = "D:\\test\\";*/
        // 2.创建这个文件夹。
       /* File file = new File(realPath);
        if (!file.exists()) {
            file.mkdirs();
        }*/
        if(uploadFile != null) {
            // 3.得到上传文件在服务器上存储的唯一路径（做处理）。
            String dstPath = UploadPic.getRealFilePath(uploadFile.getOriginalFilename(), realPath);
            // 4.创建一个服务器上的目标路径文件对象()
            File dstFile = new File(realPath + dstPath);
            // 5.完成上传文件，就是将本地文件复制到服务器上
            //5.1  SpringMVC自带方式
            uploadFile.transferTo(dstFile);
            // 5.2 流的方式。
            //UploadPic.copy(uploadFile.getInputStream(),dstFile);
            //附件入库
            Attachment a = new Attachment();
            String id = UUID.randomUUID().toString();
            a.setId(id);
            a.setFilepath(dstPath);
            a.setStuid(stuid);
            a.setFilename(uploadFile.getOriginalFilename());
            attachmentService.addAttachment(a);
            json.put("fileId",id);
            json.put("filepath",dstPath);
        }
        return json.toJSONString();
    }

    /**
     * 下载文件
     * @param fileId
     * @param rep
     * @param req
     * @return
     */
    @RequestMapping("/download")
    public String download(@RequestParam String fileId,HttpServletResponse rep,HttpServletRequest req){
        Attachment a = attachmentService.getOne(fileId);
        String path = a.getFilepath();
        String fileName = a.getFilename();
        String fileNameWithPath = attachment_path + path;
        try {
            // 转码（UTF-8-->GB2312），现在环境下的编码是UTF-8，但服务器操作系统的编码是GB2312
            fileNameWithPath = new String(fileNameWithPath.getBytes(), "UTF-8");
            // 下载文件时显示的文件名，一定要经过这样的转换，否则乱码
            fileName = URLEncoder.encode(fileName, "GB2312");
            fileName = URLDecoder.decode(fileName, "ISO8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        File file = new File(fileNameWithPath);
        FileInputStream fileinputstream = null;
        try {
            OutputStream out = rep.getOutputStream();
            fileinputstream = new FileInputStream(file);
            long l = file.length();
            int k = 0;
            byte abyte0[] = new byte[65000];
            rep.setContentType("application/x-msdownload");
            //高速浏览器压缩数据的长度：
            rep.setContentLength((int) l);
            //当Content-Type 的类型为要下载的类型时 , 这个信息头会告诉浏览器这个文件的名字和类型。
            rep.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            while ((long) k < l) {
                int j;
                j = fileinputstream.read(abyte0, 0, 65000);
                k += j;
                out.write(abyte0, 0, j);
            }
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                if(fileinputstream!=null){
                    fileinputstream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @ResponseBody
    @RequestMapping("/delImg")
    public String delImg(@RequestParam String fileId){
        JSONObject json= new JSONObject();
        Attachment a = attachmentService.getOne(fileId);
        attachmentService.delAttachment(fileId);
        String fileName = attachment_path+a.getFilepath();
        UploadPic.delFile(fileName);
        json.put("isOk","ok");
        return json.toJSONString();
    }


    @RequestMapping("/exportWord")
    public void exportWord(@RequestParam String stuid,HttpServletResponse rep,HttpServletRequest req) throws IOException {
        System.out.println(word_path);
        Student student = studentService.getOne(stuid);
        Attachment attachment = attachmentService.getAttachment(student.getId());
        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("stu", student);
        dataMap.put("img",WordUtil.getImageStr(attachment_path+attachment.getFilepath()));
        WordUtil.exportWord(req, rep, dataMap, student.getName()+"-入学信息表.doc",word_path);
    }


    @ResponseBody
    @RequestMapping("/addTest")
    public String test(){
        for(int i = 0;i < 100;i++){
            Student student = new Student();
            student.setId(UUID.randomUUID().toString());
            student.setSex("男");
            student.setName("董"+i);
            student.setClazz("JAVA108");
            student.setAge(i+"");
            studentService.addStu(student);
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("isOk","ok");
        return jsonObject.toString();
    }
}
