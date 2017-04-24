package cn.blaze.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import jxl.Workbook;
import jxl.write.Label;import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class BaseController {
	
	/**
	 * @Title printValidFormString
	 * @Description：将数据以json格式输出,可用于异步请求.编码为utf-8
	 * @param response
	 * @param info 对应key为info
	 * @param data 对应的key为data
	 * @throws IOException
	 * @user LiuLei 2017年4月24日
	 * @updater：
	 * @updateTime：
	 */
	protected void printJsonData(HttpServletResponse response, String info, String data) throws IOException {
        response.setContentType("html/txt");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        PrintWriter printWriter = response.getWriter();
        StringBuffer msg = new StringBuffer();
        msg.append("{\"info\":\"" + info + "\"");
        if (data != null && !"".equals(data)) {
            msg.append(",\"data\":\"" + data + "\"");
        } else
            msg.append(",\"data\":\"\"");
        msg.append("}");
        printWriter.write(msg.toString());
        printWriter.flush();
        printWriter.close();
    }
	
	/**
	 * @Title buildJsonMap
	 * @Description：封装为待转为json数据的Map。参数info对应key为info,参数data对应的key为data
	 * @user LiuLei 2017年3月7日
	 * @updater：
	 * @updateTime：
	 */
	protected Map<String, Object> buildJsonMap(String info, Object data){
		Map<String, Object> resultmap = new HashMap<String, Object>(2);
		resultmap.put("info", info);
		resultmap.put("data", data);
		return resultmap;
	}
	
	/**
	 * 导出为Excel文件的方法
	 * @Title exportExcel
	 * @param fileName 导出的文件名
	 * @param mapList 待写入excel的数据,每个map表示一行
	 * @param column mapList中需要写入excel的数据,column数组对应mapList的key值,数组元素顺序与excel列的顺序一致
	 * @param title excel文件的表头,数组顺序就是excel文件列的顺序
	 * @param response
	 * @user LiuLei 2017年4月19日
	 * @updater：
	 * @updateTime：
	 */
	protected void exportExcel(String fileName,List<Map<String, Object>> mapList,String[] column, String[] title, HttpServletResponse response) {
		OutputStream os = null;
		fileName = "".equals(fileName)?"导出文件":fileName; // 导出文件名称
		try {
			os = response.getOutputStream();//取得输出流
			response.reset();//清空输出流
			
			//下面是对中文文件名的处理
			response.setCharacterEncoding("ISO-8859-1");//设置相应内容的编码格式
			fileName = new String(fileName.getBytes("UTF-8"),"ISO-8859-1");
			response.setHeader("Content-Disposition","attachment;filename="+fileName+".xls");
			response.setContentType("application/msexcel");//定义输出类型
			//创建工作薄
			WritableWorkbook workbook = Workbook.createWorkbook(os);
			//创建新的一页
			WritableSheet sheet = workbook.createSheet("First Sheet",0);
			
			//创建要显示的内容,创建一个单元格，第一个参数为列坐标，第二个参数为行坐标，第三个参数为内容
			for (int i = 0; i < title.length; i++) {// 填写表头
				sheet.addCell(new Label(i,0,title[i]));
			}
			
			// 填写每个单元格
			for (int row = 0; row < mapList.size(); row++) {// row行
				Map<String, Object> map = mapList.get(row);
				for (int col = 0; col < column.length; col++) {// col列
					Label cell = new Label(col,row+1,String.valueOf(map.get(column[col])));
					sheet.addCell(cell);
				}
			}
			
			//把创建的内容写入到输出流中，并关闭输出流
			workbook.write();
			workbook.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(null != os){
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
}