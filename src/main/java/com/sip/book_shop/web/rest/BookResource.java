package com.sip.book_shop.web.rest;

import com.sip.book_shop.common.excel.service.ExcelGenerator;
import com.sip.book_shop.web.dto.BookDTO;
import com.sip.book_shop.entities.queryCriteria.BookQueryCriteria;
import com.sip.book_shop.vo.BookInfoRequest;
import com.sip.book_shop.vo.ApiResponse;
import com.sip.book_shop.common.vo.NzDataTableInput;
import com.sip.book_shop.common.vo.DataTableOutput;
import com.sip.book_shop.web.rest.base.BaseResource;
import com.sip.book_shop.service.BookApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books")
public class BookResource implements BaseResource<ApiResponse<DataTableOutput<BookDTO>>, ApiResponse<BookDTO>, BookInfoRequest, BookInfoRequest> {

    private final BookApiService bookApiService;

    @Override
    public ResponseEntity<ApiResponse<DataTableOutput<BookDTO>>> findAll(NzDataTableInput dataTableInput) {
        BookQueryCriteria criteria = BookQueryCriteria.createBookQueryCriteria(dataTableInput);
        DataTableOutput<BookDTO> dataTableOutput = DataTableOutput.of(bookApiService.getAllResult(criteria));
        return ResponseEntity.ok(ApiResponse.success(dataTableOutput));
    }

    @Override
    public ResponseEntity<ApiResponse<BookDTO>> findById(int id) {
        return ResponseEntity.ok(ApiResponse.success(bookApiService.findById(id)));
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> create(BookInfoRequest request) {
        bookApiService.addNew(request);
        ApiResponse<Void> responseBody = ApiResponse.created("Created book successfully!");
        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> update(BookInfoRequest request, int id) {
        bookApiService.update(request, id);
        return ResponseEntity.ok(ApiResponse.success("Updated book successfully!"));
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> delete(int id) {
        bookApiService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted book successfully!"));
    }

    @Override
    public ResponseEntity<byte[]> generateExcel() {
        List<BookDTO> books = bookApiService.getAll();
        byte[] excelContent = ExcelGenerator.generate(books, BookDTO.class);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        httpHeaders.setContentDispositionFormData("attachment", "book_export.xlsx");
        return ResponseEntity.ok().headers(httpHeaders).body(excelContent);
    }
}
