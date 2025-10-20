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
public class FileUpImg {
	List<MultipartFile> fileList;
	int atResource;
	int fileSize;
	boolean hasFirst;
}
