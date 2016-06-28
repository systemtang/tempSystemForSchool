package com.cdgy.graduation.servlet;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.RequestContext;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.servlet.ServletRequestContext;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

@WebServlet("/file/excel")
public class excelFileUpload extends HttpServlet{
	
	public excelFileUpload(){
		super();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		req.setCharacterEncoding("utf-8");
		
		RequestContext rc = new ServletRequestContext(req);
		
		ServletOutputStream out=resp.getOutputStream();
		
		if(FileUpload.isMultipartContent(rc)){
			DiskFileItemFactory factory = new DiskFileItemFactory();
			//设置文件缓存路径
			
			String dir = "/Users/yi/Desktop/唐荣杰demo/mydemo/school/";
			File filedir = new File(dir+"/temp");
			if(!filedir.exists()){
				filedir.mkdir();
			}
			
			factory.setRepository(filedir);
			
			factory.setSizeThreshold(1024*1024*2);
			
			ServletFileUpload sfu = new ServletFileUpload(factory); 
			
			sfu.setSizeMax(1024*1024*2);
			List items = new ArrayList();
			
			try {
				items = sfu.parseRequest(req);
				
			} catch (Exception e) {
				System.out.println("error");
			}
			
			Iterator it = items.iterator();
			while(it.hasNext()){
				FileItem fi = (FileItem) it.next();
				//如果是普通字段
				if(fi.isFormField()){
					System.out.println("type1 = "+fi.getFieldName()+"::"+fi.getName()+"::"+new String(fi.getString().getBytes("iso8859-1"),"utf-8"));
				}else{
					System.out.println("type2 = "+fi.getFieldName()+"::"+fi.getName()+"::"+fi.isInMemory()+"::"+fi.getContentType()+"::"+fi.getSize());
				}
				
				//保存文件
				if(fi.getName()!=null && fi.getSize()!=0){
					File fullFile = new File(fi.getName());
					File newFile = new File(dir+"/temp/"+fullFile.getName());//这里开始可以获取到文件对象，可以进行excel表格的读取
					try {
						fi.write(newFile);
						System.out.println("文件保存成功！");
					} catch (Exception e) {
						System.out.println("error::");
						e.printStackTrace();
					}
				}else{
					System.out.println("文件没有选择或者文件为空！");
				}
				
				if(fi.getName()!=null && fi.getSize()!=0){
					try {
						// 获取文件对象
						File fullFile = new File(fi.getName());
						FileInputStream is = new FileInputStream(fullFile);
						Workbook workbook = WorkbookFactory.create(is);
						int sheetCount = workbook.getNumberOfSheets();//获取sheet数量
						//开始遍历每个sheet
						for(int i = 0;i < sheetCount;i++){
							Sheet sheet = workbook.getSheetAt(i);
							int rowCount = sheet.getPhysicalNumberOfRows();//获取总行数
							
							//检查第一行的格式是否符合要求
							//test:要求格式为【姓名，班级，学号，序号，入学时间，家属电话(可空)】
							Row topRow = sheet.getRow(0);
							boolean checkTopStyle = false;
							int cellTopCount = topRow.getPhysicalNumberOfCells();//获取总列数
							
							System.out.println("输出首行数据：");
							List<String> errMsg = new ArrayList<String>();
							//遍历每列
							for(int c = 0;c<cellTopCount;c++){
								Cell cell = topRow.getCell(c);
								int cellType = cell.getCellType();
								switch (cellType) {
								case Cell.CELL_TYPE_STRING:
									if(cell.getStringCellValue().toString().equals("姓名") && c==0 ){
										//第一列合法
										System.out.println("第一列合法");
										System.out.println(cell.getStringCellValue()+";");
									}else if(cell.getStringCellValue().toString().equals("班级") && c==1 ){
										//第二列合法
										System.out.println("第二列合法");
										System.out.println(cell.getStringCellValue()+";");
									}else if(cell.getStringCellValue().toString().equals("学号") && c==2 ){
										//第三列合法
										System.out.println("第三列合法");
										System.out.println(cell.getStringCellValue()+";");
									}else if(cell.getStringCellValue().toString().equals("序号") && c==3 ){
										//第四列合法
										System.out.println("第四列合法");
										System.out.println(cell.getStringCellValue()+";");
									}else if(cell.getStringCellValue().toString().equals("入学时间") && c==4 ){
										//第五列合法
										System.out.println("第五列合法");
										System.out.println(cell.getStringCellValue()+";");
									}else if(cell.getStringCellValue().toString().equals("家属电话") && c==5 ){
										//第六列合法
										System.out.println("第六列合法");
										System.out.println(cell.getStringCellValue()+";");
									}
									break;

								default:
									String temp = "首行"+ (c+1) +"列出现了非法字符，请全部换为文本格式";
									errMsg.add(temp);
									break;
								}
							}
							
							if(errMsg.size()!=0){
								//出现了错误
								String allError = "";
								for (String temp : errMsg) {
									allError += temp + ";";
								}
								out.print(allError);
							}
							
							if(checkTopStyle){
								//遍历每行(首行除外)
								for(int j =1;j < rowCount;j++){
									Row row = sheet.getRow(j);
									int cellCount = row.getPhysicalNumberOfCells();//获取总列数
									//遍历每列
									for(int c = 0;c<cellCount;c++){
										
									}
								}
							}
							
						}
						
					} catch (InvalidFormatException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	public static void main(String[] args) {
		String [] a = {"a","b","c","d"};
		int s = a.in
	}
}
