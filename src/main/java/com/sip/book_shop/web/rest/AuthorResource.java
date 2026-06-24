package com.sip.book_shop.web.rest;

import com.sip.book_shop.common.excel.service.ExcelGenerator;
import com.sip.book_shop.web.dto.AuthorDTO;
import com.sip.book_shop.entities.queryCriteria.AuthorQueryCriteria;
import com.sip.book_shop.vo.ApiResponse;
import com.sip.book_shop.common.vo.NzDataTableInput;
import com.sip.book_shop.common.vo.DataTableOutput;
import com.sip.book_shop.web.rest.base.BaseResource;
import com.sip.book_shop.service.AuthorApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/authors")
public class AuthorResource implements BaseResource<ApiResponse<DataTableOutput<AuthorDTO>>, ApiResponse<AuthorDTO>, AuthorDTO, AuthorDTO> {

    private final AuthorApiService authorApiService;

    @Override
    public ResponseEntity<ApiResponse<DataTableOutput<AuthorDTO>>> findAll(NzDataTableInput dataTableInput) {
        AuthorQueryCriteria criteria = AuthorQueryCriteria.createAuthorQueryCriteria(dataTableInput);
        DataTableOutput<AuthorDTO> dataTableOutput = DataTableOutput.of(authorApiService.findAll(criteria));
        return ResponseEntity.ok(ApiResponse.success(dataTableOutput));
    }

    @Override
    public ResponseEntity<ApiResponse<AuthorDTO>> findById(int id) {
        return ResponseEntity.ok(ApiResponse.success(authorApiService.findById(id)));
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> create(AuthorDTO request) throws BindException {
        authorApiService.addNew(request);
        ApiResponse<Void> responseBody = ApiResponse.created("Created author successfully!");
        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> update(AuthorDTO request, int id) throws BindException {
        authorApiService.update(request, id);
        return ResponseEntity.ok(ApiResponse.success("Updated author successfully!"));
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> delete(int id) {
        authorApiService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted author successfully!"));
    }

    @Override
    public ResponseEntity<byte[]> generateExcel() {
        List<AuthorDTO> authors = authorApiService.getAll();
        byte[] excelContent = ExcelGenerator.generate(authors, AuthorDTO.class);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        httpHeaders.setContentDispositionFormData("attachment", "author_export.xlsx");
        return ResponseEntity.ok().headers(httpHeaders).body(excelContent);
    }
}
