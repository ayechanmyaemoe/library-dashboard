package com.sip.book_shop.common.excel;

import com.sip.book_shop.common.excel.query.ExcelColumn;
import com.sip.book_shop.common.exceptions.InternalServerException;
import com.sip.book_shop.dto.AuthorDTO;
import com.sip.book_shop.dto.BookDTO;
import com.sip.book_shop.dto.CategoryDTO;
import com.sip.book_shop.dto.RoleDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class ExcelGenerator {

    public static <T> byte[] generate(List<T> dataList, Class<T> entityClass) {
        if (dataList == null || dataList.isEmpty()) {
            return new byte[0];
        }

        String className = entityClass.getSimpleName().replace("DTO", "");

        try(Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet(className + " Data");

            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerFont.setFontHeightInPoints((short) 12);

            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            List<Field> annotatedFields = Stream.of(entityClass.getDeclaredFields())
                    .filter(field -> field.isAnnotationPresent(ExcelColumn.class))
                    .sorted(Comparator.comparingInt(f -> f.getAnnotation(ExcelColumn.class).index()))
                    .toList();

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < annotatedFields.size(); i++) {
                ExcelColumn annotation = annotatedFields.get(i).getAnnotation(ExcelColumn.class);

                Cell cell = headerRow.createCell(i);
                cell.setCellValue(annotation.name());
                cell.setCellStyle(headerCellStyle);
            }

            int rowIndex = 1;
            for (T data : dataList) {
                Row row = sheet.createRow(rowIndex++);
                for (int i = 0; i < annotatedFields.size(); i++) {
                    Field field = annotatedFields.get(i);
                    field.setAccessible(true);
                    Object value = field.get(data);

//                    if(value instanceof AuthorDTO) {
//                        value = ((AuthorDTO) value).getName();
//                    } else if (value instanceof CategoryDTO) {
//                        value = ((CategoryDTO) value).getName();
//                    } else if (value instanceof RoleDTO) {
//                        value = ((RoleDTO) value).getName();
//                    }
                    ExcelColumn annotation = field.getAnnotation(ExcelColumn.class);
                    if (value != null && !annotation.nestedField().isEmpty()) {
                        try {
                            Field nestedField = value.getClass().getDeclaredField(annotation.nestedField());
                            nestedField.setAccessible(true);
                            value = nestedField.get(value);
                        } catch (NoSuchFieldException e) {
                            value = "[Invalid Nested Field]";
                        }
                    }

                    Cell cell = row.createCell(i);
                    if (value != null) {
                        cell.setCellValue(value.toString());
                    }
                }
            }

            for (int i = 0; i < annotatedFields.size(); i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new InternalServerException(e.getMessage());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
