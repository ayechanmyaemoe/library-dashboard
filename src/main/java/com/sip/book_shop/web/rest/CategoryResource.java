package com.sip.book_shop.web.rest;

import com.sip.book_shop.common.excel.ExcelGenerator;
import com.sip.book_shop.dto.CategoryDTO;
import com.sip.book_shop.entities.queryCriteria.CategoryQueryCriteria;
import com.sip.book_shop.vo.ApiResponse;
import com.sip.book_shop.common.vo.NzDataTableInput;
import com.sip.book_shop.common.vo.DataTableOutput;
import com.sip.book_shop.web.rest.base.BaseResource;
import com.sip.book_shop.services.CategoryApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryResource implements BaseResource<ApiResponse<DataTableOutput<CategoryDTO>>, ApiResponse<CategoryDTO>, CategoryDTO, CategoryDTO> {

    @Autowired
    private CategoryApiService categoryApiService;

    @Override
    public ResponseEntity<ApiResponse<DataTableOutput<CategoryDTO>>> findAll(NzDataTableInput dataTableInput) {
        CategoryQueryCriteria criteria = CategoryQueryCriteria.createCategoryQueryCriteria(dataTableInput);
        DataTableOutput<CategoryDTO> dataTableOutput = DataTableOutput.of(categoryApiService.getAllResult(criteria));
        return ResponseEntity.ok(ApiResponse.success(dataTableOutput));
    }

    @Override
    public ResponseEntity<ApiResponse<CategoryDTO>> findById(int id) {
        return ResponseEntity.ok(ApiResponse.success(categoryApiService.findById(id)));
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> create(CategoryDTO request) throws BindException {
        categoryApiService.addNew(request);
        ApiResponse<Void> responseBody = ApiResponse.created("Created category successfully!");
        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> update(CategoryDTO request, int id) throws BindException {
        categoryApiService.update(request, id);
        return ResponseEntity.ok(ApiResponse.success("Updated category successfully!"));
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> delete(int id) {
        categoryApiService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted category successfully!"));
    }

    @Override
    public ResponseEntity<byte[]> generateExcel() {
        List<CategoryDTO> categories = categoryApiService.getAll();
        byte[] excelContent = ExcelGenerator.generate(categories, CategoryDTO.class);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        httpHeaders.setContentDispositionFormData("attachment", "category_export.xlsx");
        return ResponseEntity.ok().headers(httpHeaders).body(excelContent);
    }
}
