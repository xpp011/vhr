package cn.xpp011.vhr.utils;

import cn.xpp011.vhr.model.*;
import org.apache.poi.hpsf.DocumentSummaryInformation;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.hssf.usermodel.*;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class POIUtil {

    static String[] titles={"姓名","性别","工号","出生日期","身份证号码","婚姻状况","民族","籍贯","政治面貌","电子邮件","联系地址","所属部门","职位","聘用形式","入职日期","转正日期","合同起始日期","合同终止日期","合同期限","毕业院校","专业","最高学历","职称"};

    public static ResponseEntity<byte[]> employeeExcel(List<Employee> list){
        //1，创建一个Excel文档
        HSSFWorkbook workbook = new HSSFWorkbook();

        //2，创建文档的摘要
        workbook.createInformationProperties();

        //3，获取文档信息并配置
        DocumentSummaryInformation docInfo = workbook.getDocumentSummaryInformation();

        //文档类别
        docInfo.setCategory("员工信息");

        //文档管理员
        docInfo.setManager("xpp011");
        //公司信息
        docInfo.setCompany("www.xpp011.cn");

        //4，获取文档摘要信息
        SummaryInformation summInfo = workbook.getSummaryInformation();

        //文档标题
        summInfo.setTitle("员工信息表");

        //文档作者
        summInfo.setAuthor("xpp011");

        //文档备注
        summInfo.setComments("本文档由xpp011提供");

        //创建标题行样式
        HSSFCellStyle headerStyle=workbook.createCellStyle();
        //设置背景色为黄色
        headerStyle.setFillForegroundColor(IndexedColors.YELLOW.index);
        //前景色
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        //设置有日期的单元格的日期格式
        HSSFCellStyle dateFormat=workbook.createCellStyle();
        dateFormat.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy"));
        //创建一张表
        HSSFSheet sheet = workbook.createSheet("员工信息表");



        //6,在表内创建一行  第一行为标题行
        HSSFRow r0 = sheet.createRow(0);
        //创建列  循环遍历titles设立抬头
        for (int i = 0; i < titles.length; i++) {
            HSSFCell cell = r0.createCell(i);
            cell.setCellStyle(headerStyle);
            cell.setCellValue(titles[i]);
            //设置表格单元格大小
            sheet.setColumnWidth(i,25*256);
        }

        //开始插入数据
        for (int i = 0; i < list.size(); i++) {
            Employee emp=list.get(i);
            HSSFRow row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(emp.getName());
            row.createCell(1).setCellValue(emp.getGender());
            row.createCell(2).setCellValue(emp.getWorkID());
            //出生日期设置日期格式
            HSSFCell cell3 = row.createCell(3);
            cell3.setCellStyle(dateFormat);
            cell3.setCellValue(emp.getBirthday());

            row.createCell(4).setCellValue(emp.getIdCard());
            row.createCell(5).setCellValue(emp.getWedlock());
            row.createCell(6).setCellValue(emp.getNation().getName());
            row.createCell(7).setCellValue(emp.getNativePlace());
            row.createCell(8).setCellValue(emp.getPoliticsstatus().getName());
            row.createCell(9).setCellValue(emp.getEmail());
            row.createCell(10).setCellValue(emp.getAddress());
            row.createCell(11).setCellValue(emp.getDepartment().getName());
            row.createCell(12).setCellValue(emp.getPosition().getName());
            row.createCell(13).setCellValue(emp.getEngageForm());
            //设置入职日期
            HSSFCell cell14 = row.createCell(14);
            cell14.setCellStyle(dateFormat);
            cell14.setCellValue(emp.getBeginDate());

            //设置转正日期
            HSSFCell cell15 = row.createCell(15);
            cell15.setCellStyle(dateFormat);
            cell15.setCellValue(emp.getConversionTime());

            //合同起始日期
            HSSFCell cell16 = row.createCell(16);
            cell16.setCellStyle(dateFormat);
            cell16.setCellValue(emp.getBeginContract());

            //合同终止日期
            HSSFCell cell17 = row.createCell(17);
            cell17.setCellStyle(dateFormat);
            cell17.setCellValue(emp.getEndContract());

            row.createCell(18).setCellValue(emp.getContractTerm());
            row.createCell(19).setCellValue(emp.getSchool());
            row.createCell(20).setCellValue(emp.getSpecialty());
            row.createCell(21).setCellValue(emp.getTiptopDegree());
            row.createCell(22).setCellValue(emp.getJobLevel().getName());
        }
        //创建一个Byte类型的字节流
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        HttpHeaders header=new HttpHeaders();
        try {
            //设置文件名  并设置编码格式
            header.setContentDispositionFormData("attachment",new String("员工表.xls".getBytes(StandardCharsets.UTF_8),"ISO-8859-1"));
            //设置响应头为文件下载
            header.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            //将数据写到输出流中
            workbook.write(baos);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //传一个byte的数组    设置响应头  设置状态码
        return new ResponseEntity<byte[]>(baos.toByteArray(),header, HttpStatus.CREATED);
    }

    public static List<Employee> importEmp (MultipartFile file, List<Nation> nations, List<Politicsstatus> politicsstatus, List<Department> departmentAll, List<Position> positionAll, List<JobLevel> jobLevelAll) throws Exception{
        List<Employee> list=new ArrayList<>();
        Employee employee=null;
        //1,创建WorkBook对象
        HSSFWorkbook workbook = new HSSFWorkbook(file.getInputStream());

        //2,获取表格数量
        int numberOfSheets = workbook.getNumberOfSheets();

        for (int i = 0; i < numberOfSheets; i++) {
            //3,获取表单
            HSSFSheet sheetAt = workbook.getSheetAt(i);

            //4，获取表单中的行数
            int physicalNumberOfRows = sheetAt.getPhysicalNumberOfRows();

            //跳过标题行
            for (int j = 1; j < physicalNumberOfRows; j++) {
                //5,获取数据行
                HSSFRow row = sheetAt.getRow(j);
                //判断是否为空行
                if (row==null){
                    continue;
                }

                //6,获取列数
                //初始化Employee
                employee=new Employee();
                int physicalNumberOfCells = row.getPhysicalNumberOfCells();
                for (int k = 0; k < physicalNumberOfCells; k++) {
                    HSSFCell cell = row.getCell(k);
                    //首先匹配字符串类型的数据
                    //日期格式的统一放在最后去处理
                    switch (cell.getCellType()){
                        case STRING:
                            String cellValue = cell.getStringCellValue();
                            switch (k){
                                case 0:
                                    employee.setName(cellValue);//姓名
                                    break;
                                case 1:
                                    employee.setGender(cellValue);//性别
                                    break;
                                case 2:
                                    employee.setWorkID(cellValue);//工号
                                    break;
                                case 4:
                                    employee.setIdCard(cellValue);//身份证号
                                    break;
                                case 5:
                                    employee.setWedlock(cellValue);//婚姻状况
                                    break;
                                case 6:
                                    //重写equals  使其只要name相同则判定两个对象相同
                                    //使用IndexOf方法时就会寻找名字相同的对象返回它的Index
                                    int natsIndex = nations.indexOf(new Nation(cellValue));
                                    //通过get方法得到对应的Nation 并获取它的ID
                                    employee.setNationId(nations.get(natsIndex).getId());//民族id
                                    break;
                                case 7:
                                    employee.setNativePlace(cellValue);//籍贯
                                    break;
                                case 8:
                                    //如法炮制
                                    int polIndex = politicsstatus.indexOf(new Politicsstatus(cellValue));
                                    employee.setPoliticId(politicsstatus.get(polIndex).getId());//政治面貌
                                    break;
                                case 9:
                                    employee.setEmail(cellValue);
                                    break;
                                case 10:
                                    employee.setAddress(cellValue);
                                    break;
                                case 11:
                                    int depIndex = departmentAll.indexOf(new Department(cellValue));
                                    employee.setDepartmentId(departmentAll.get(depIndex).getId());//所属部门
                                    break;
                                case 12:
                                    int posIndex = positionAll.indexOf(new Position(cellValue));
                                    employee.setPosId(positionAll.get(posIndex).getId());//职位
                                    break;
                                case 13:
                                    employee.setEngageForm(cellValue);//聘用形式
                                    break;
                                case 19:
                                    employee.setSchool(cellValue);//毕业院校
                                    break;
                                case 20:
                                    employee.setSpecialty(cellValue);//专业
                                    break;
                                case 21:
                                    employee.setTiptopDegree(cellValue);//最高学历
                                    break;
                                case 22:
                                    int jobIndex = jobLevelAll.indexOf(new JobLevel(cellValue));
                                    employee.setJobLevelId(jobLevelAll.get(jobIndex).getId());//职称

                            }
                        default:{
                            //日期特殊处理 getDateCellValue方法得到日期
                            switch (k){
                                case 3:
                                    employee.setBirthday(cell.getDateCellValue());//出生日期
                                    break;
                                case 14:
                                    employee.setBeginDate(cell.getDateCellValue());//入职日期
                                    break;
                                case 15:
                                    employee.setConversionTime(cell.getDateCellValue());//转正日期
                                    break;
                                case 16:
                                    employee.setBeginContract(cell.getDateCellValue());//合同起始日期
                                    break;
                                case 17:
                                    employee.setEndContract(cell.getDateCellValue());//合同终止日期
                                    break;
                                //非日期格式  double格式
                                case 18:
                                    employee.setContractTerm(cell.getNumericCellValue());
                                    break;
                            }
                        }
                        break;
                    }
                }
                //将设置好元素的employee添加到list中
                list.add(employee);
            }
        }

        return list;
    }
}
