package com.ping.student.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Value;
import sun.misc.BASE64Encoder;

public class WordUtil
{
	// 配置信息,代码本身写的还是很可读的,就不过多注解了
	private static Configuration configuration = null;
	// 利用WordUtils的类加载器动态获得模板文件的位置
	//private static final String templateFolder = WordUtil.class.getResource("/static/tools").getPath();
	static
	{
		configuration = new Configuration();
		configuration.setDefaultEncoding("utf-8");
		/*try
		{
			// 设置模板的路径
			configuration.setDirectoryForTemplateLoading(new File(templateFolder));
		} catch (IOException e)
		{
			e.printStackTrace();
		}*/
	}

	private static File createDoc(Map<?, ?> dataMap, Template template)
	{
		String name = "test.doc";
		File f = new File(name);
		try
		{
			Writer w = new OutputStreamWriter(new FileOutputStream(f), "utf-8");
			template.process(dataMap, w);
			w.close();
		} catch (Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return f;
	}

	public static void exportWord(HttpServletRequest request, HttpServletResponse response, Map<String, Object> dataMap,
			String fileName,String word_path) throws IOException
	{
		try
		{
			// 下载文件时显示的文件名，一定要经过这样的转换，否则乱码
			fileName = URLEncoder.encode(fileName, "GB2312");
			fileName = URLDecoder.decode(fileName, "ISO8859-1");
		} catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		Template template = null;
		configuration.setDirectoryForTemplateLoading(new File(word_path));
		template = configuration.getTemplate("exportStu.ftl");
		File file = null;
		InputStream fin = null;
		ServletOutputStream out = null;
		try
		{
			// 调用工具类的createDoc方法生成Word文档
			file = createDoc(dataMap, template);
			fin = new FileInputStream(file);
			// response.setCharacterEncoding("utf-8");
			response.setContentType("application/msword");
			// 设置浏览器以下载的方式处理该文件名
			/*
			 * response.setHeader("Content-Disposition",
			 * "attachment;filename=".concat(String.valueOf(URLEncoder.encode(fileName,
			 * "UTF-8"))));
			 */
			response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
			out = response.getOutputStream();
			byte[] buffer = new byte[512]; // 缓冲区
			int bytesToRead = -1;
			// 通过循环将读入的Word文件的内容输出到浏览器中
			while ((bytesToRead = fin.read(buffer)) != -1)
			{
				out.write(buffer, 0, bytesToRead);
			}
		} finally
		{
			if (fin != null)
				fin.close();
			if (out != null)
				out.close();
			if (file != null)
				file.delete(); // 删除临时文件
		}
	}

	public static String getImageStr(String imgPath) {
		InputStream in = null;
		byte[] data = null;
		try {
			in = new FileInputStream(imgPath);
			data = new byte[in.available()];
			in.read(data);
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		BASE64Encoder encoder = new BASE64Encoder();
		return encoder.encode(data);
	}
}