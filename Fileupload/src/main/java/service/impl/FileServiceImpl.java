package service.impl;

import java.io.File;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import service.face.FileService;

public class FileServiceImpl implements FileService {

	@Override
	public boolean fileupload(HttpServletRequest req) {

		System.out.println("FileServoce fileupload() 호출");
		//---------------------------------------------------
		
		//1. 파일 업로드 형식의 인코딩이 맞는지 검사
		//	요청 메시지의 content-type이 multipart/form-data가 맞는지 확인
		boolean isMultipart = ServletFileUpload.isMultipartContent(req);
		
		if(!isMultipart) {
			//multipart 데이터가 아님
			System.out.println("fileService !Multipart : " + isMultipart);
			
			//fileupload()메소드 중단시키기
			return false;
		}

		//-----------------------------------------------------------------------
		
		//2. 업로드된 데이터를 처리하는 방법을 설정
		
		//FileItem
		//	클라이언트가 전송한 전달 파라미터들을 객체로 만든 것
		//	폼 필드, 파일 데이터 전부를 객체로 저장할 수 있음
		//	**폼 필드 : 파일이 아닌 전달 파라미터(input 태그 데이터)
		
		//FileItemFactory
		//	FileItem객체를 생성하는 방식을 설정해두는 클래스
		
		//DiskFileItemFactory
		//	하드디스크(HDD) 기반으로 FileItem을 처리하는 팩토리 클래스
		//	업로드된 파일을 하드디스크에 임시 저장하여 처리하도록 설정
		//	파일의 용량이 작으면 메모리에서 처리
		//	파일의 용량이 크면 하드디스크에서 처리
		
		//업로드 데이터 처리 방법 설정 객체
		DiskFileItemFactory factory = new DiskFileItemFactory();
		
		//-----------------------------------------------------------------------

		//3. 업로드된 FileItem의 용량(크기)이 설정값보다 작으면 메모리에서 처리
		int maxMem = 1 * 1024 * 1024;	//1MB == 1048576byte 단위로 넣어줘야 함
		factory.setSizeThreshold(maxMem);
		
		//-----------------------------------------------------------------------

		//4. 메모리 처리 사이즈보다 크면 임시파일을 만들어서 HDD로 처리
		//	임시파일을 저장할 폴더를 설정할 수 있음
		
		//서블릿 컨텍스트 객체
		//	요청받은 정보를 처리하는 서블릿의 컨텍스트 환경을 확인할 수 있음
		ServletContext context = req.getServletContext();
		
		//서버에 배포가 서블릿의 실제 서버 경로를 알아옴
		String path = context.getRealPath("tmp");
		System.out.println("FileService fileupload() - tmp 경로 : " + path);
		
		//임시파일을 저장할 폴더의 File 객체
		File tmpRespository = new File(path);
		 
		//폴더 생성하기
		//	폴더가 없으면 폴더 생성, 있으면 생성하지 않음(에러 없음)
		tmpRespository.mkdir();
		
		//임시 파일을 저장할 폴더를 팩토리객체에 설정하기
		factory.setRepository(tmpRespository);
		
		//-----------------------------------------------------------------------
		
		//5. 파일 업로드를 수행하는 객체 설정하기
		
		//DiskFileItemFactory에 적용한 설정값을 반영하여 객체 생성
		ServletFileUpload upload = new ServletFileUpload(factory);
		
		//최대 업로드 허용 사이즈 설정
		int maxFile = 10 * 1024 * 1024; //10MB
		upload.setFileSizeMax(maxFile);
				
		//-----------------------------------------------------------------------

		//---- 파일 업로드 준비 완료 ----
		
		//-----------------------------------------------------------------------
		
		//6. 파일 업로드 처리
		//	전달된 요청 파라미터 분석(추출)하기
		//	폼필드, 파일 전부 분석
		
		List<FileItem> items = null;
		
		try {
			//요청객체에 담겨있는 전달 파라미터를 파싱
			//	폼 필드를 추출
			//	파일도 업로드를 수행
			items = upload.parseRequest(req);
			
		} catch (FileUploadException e) {
			e.printStackTrace();
		}
		
		for(FileItem item : items) {
			System.out.println(item);
		}
		
		
		//파일 업로드 처리 완료
		return true;
	}
	
}
