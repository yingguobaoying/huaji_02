package com.huaji.galgamebyhuaji.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileUpRar {
	private List<MultipartFile> fileList;
	private List<String> fileName;
	private int atResource;
	private int fileSize;
	private String notes;
}
