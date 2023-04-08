package service.impl;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import common.JDBCTemplate;
import dto.ParamData;
import dto.UploadFile;
import service.face.FileServiceRv;

public class FileServiceImplRv implements FileServiceRv {
	
	@Override
	public boolean fileupload(HttpServletRequest req) {
		System.out.println("FileServiceRv fileupload() 호출");
		//-----------------------------------------------------
		
		//1. 파일 업로드 형식의 인코딩이 맞는지 검사
		//	요청 메시지의 content-type이 multipart/form-data가 맞는지 확인
		boolean isMultipart = ServletFileUpload.isMultipartContent(req);
		
		if(!isMultipart) {
			//multipart 데이터가 아님
			System.out.println("fileService !Multipart : " + isMultipart);
			
			//메소드 중단시키기
			return false;
		
		}
		
		//-----------------------------------------------------

		//2. 업로드된 데이터를 처리하는 방법을 설정
		
		//FileItem
		//	클라이언트가 전송한 전달 파라미터들을 객체로 만든 것
		//	폼 필드, 파일 데이터 전부를 객체로 저장할 수 있음
		//	** 폼 필드 : 파일이 아닌 전달 파라미터(input 태그 데이터)
		
		//FileItemFactory
		//	FileItem 객체를 생성하는 방식을 설정해두는 클래스
		
		//DiskFileItemFactory
		//	하드디스크(HDD) 기반으로 FileItem을 처리하는 팩토리 클래스
		//	업로드된 파일을 하드디스크에 임시 저장하여 처리하도록 설정
		//	파일의 용량이 작으면 메모리에서 처리
		//	파일의 용량이 크면 하드디스크에서 처리
		
		//업로드 데이터 처리 방법 설정 객체
		DiskFileItemFactory factory = new DiskFileItemFactory();		
		
		//-----------------------------------------------------

		//3. 업로드된 FileItem의 용량(크기)이 설정값보다 작으면 메모리에서 처리
		int maxMem = 1 * 1024 * 1024;	//1MB -> byte 단위로 넣어줘야함
		factory.setSizeThreshold(maxMem);
		
		//-----------------------------------------------------

		//4. 메모리 처리 사이즈보다 크면 임시파일을 만들어서 HDD로 처리
		//	임시 파일을 저장할 폴더를 설정할 수 있음
		
		//서블릿 컨텍스트 객체
		//	요청받은 정보를 처리하는 서블릿의 컨텍스트 환경을 확인할 수 있음
		ServletContext context = req.getServletContext();
		
		//서버에 배포된 서블릿의 실제 서버 경로를 알아옴
		String path = context.getRealPath("tmp");
		System.out.println("FileService fileupload() - tmp 경로 : " + path);
		
		//임시파일을 저장할 폴더의 File 객체
		File tmpRespository = new File(path);
		
		//폴더 생성하기
		//	폴더가 없으면 폴더 생성, 있으면 생성하지 않음(에러 없음)
		tmpRespository.mkdir();
		
		//-----------------------------------------------------

		//5. 파일 업로드를 수행하는 객체 설정하기
		
		//DiskFileItemFactory에 적용한 설정값을 반영하여 객체 생성
		ServletFileUpload upload = new ServletFileUpload(factory);
		
		//!!!!업로드 허용은 10MB까지고 1MB까지는 메모리에서 그 이상은 하드디스크에서 처리한다는건가?
		
		//최대 업로드 허용 사이즈 설정
		int maxFile = 10 * 1024 * 1024; //10MB
		upload.setFileSizeMax(maxFile);
		
		//-----------------------------------------------------

		//---- 파일 업로드 준비 완료 ----
		
		//-----------------------------------------------------

		//6. 파일 업로드 처리
		//	전달된 요청 파라미터 분석(추출)하기
		//	폼필드, 파일 전부 분석
		List<FileItem> items = null;
		
		try {
			
			//요청 객체에 담겨있는 전달 파라미터를 파싱
			//	폼 필드를 추출
			//	파일도 업로드를 수행
			items = upload.parseRequest(req);
			
			
			
		} catch (FileUploadException e) {
			e.printStackTrace();
		}

		for(FileItem item : items) {
			System.out.println(item);
		}
		
		//-----------------------------------------------------
		
		//7. 파싱된 전달 파라미터 데이터 처리하기
		//	-> List<FileItem> 객체에 파일과 폼필드 데이터가 들어있음
		//	-> 요청데이터(FileItem) 종류
		//		- 빈 파일 : 용량이 0인 파일
		//		- 폼 필드, form-data : 일반적인 전달 파라미터 -> 전달된 데이터들의 DB에 INSERT함
		//		- 파일 -> 크게 두 가지 방법이 있음		!!!!2가지 방법이란게 무슨 말이지
		//			파일은 디스크에 저장
		//			웹 서버의 로컬 폴더
		//			파일의 정보는 DB에 INSERT 함
		
		//폼필드 전달 파라미터를 저장할 DTO 객체
		ParamData paramData = new ParamData();
		
		//파일 정보를 저장할 DTO 객체
		UploadFile uploadFile = new UploadFile();
		
		//파일아이템의 리스트 반복자
		Iterator<FileItem> iter = items.iterator();		//!!!!iterator는 왜 필요하지
		
		while(iter.hasNext()) {
			
			//전달 파라미터를 저장한 FileItem객체를 하나씩 꺼내서 적용하기
			FileItem item = iter.next();
			
			//---- 1. 빈 파일에 대한 처리 ----
			//	비어있다면 사이즈는 0일 것 -> 사이즈를 알기 위해 if문으로 getSize()
			if(item.getSize() <= 0) {
				
				//만약 0 이하면 다음 파일 처리를 해야하니까 if문을 빠져나가서 다시 위에서부터 코드를 시작해야 함
				//continue를 이용해 빈 파일을 무시하고 다음 FileItem 처리로 넘어가기
				continue;
				
			}

			//---- 2. 폼 필드에 대한 처리 ----
			if(item.isFormField()) {
				
				//폼필드는 key=value 쌍으로 전달됨
//				item.getFieldName();	//getFieldName은 key 값
//				item.getString();		//getString은 value의 값
				
				String key = item.getFieldName();
				String value = null;
				
				try {
					
					//한글 정보를 받아들이는 방법은 getString으로 해야 함
					value = item.getString("UTF-8");
					
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				
				System.out.println("FileService fileupload() - key : " + key);
				System.out.println("FileService fileupload() - value : " + value);
				
				//전달파라미터의 name(key)에 맞게 DTO의 setter를 호출
				if("title".equals(key)) {
					paramData.setTitle(value);
				} else if("data1".equals(key)) {
					paramData.setData1(value);
				} else if("data2".equals(key)) {
					paramData.setData2(value);
				}

			}
			
			//---- 3. 파일에 대한 처리 ----
			if(!item.isFormField()) {
				
				//업로드된 파일은 서버 로컬 HDD에 저장
				//	파일의 이름을 원본과 다르게 바꿔서 저장

				//서버는 원본 파일 이름, 바꾼 파일 이름 둘 다 기억하고 있어야 함
				//	-> DB에 원본 이름, 저장한 이름 모두 INSERT(저장)
				
				//파일 보호를 위해 날짜 이름으로 문자열 변환해주기
				//	날짜 -> 문자열 변환(Date -> String)
				//	SimpleDataFormat 클래스 이용
				
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssS");
				String rename = sdf.format(new Date());
				
				//getName()을 해주면 name을 가져오고 null일 것.. 파일명..? 무슨 말이지
				System.out.println("FileService fileService() - 원본파일명 : " + item.getName());
				System.out.println("FileService fileService() - 저장파일명 : " + rename);
				
				//임시 보관하고 있는 파일(tmp폴더) 실제 업로드 저장소로 옮기기
				
				//!!!!!위에 임시파일은 뭐고 이 실제 파일 저장소는 뭐지
				
				//실제 파일 저장소
				File uploadFolder = new File(context.getRealPath("upload"));
				uploadFolder.mkdir();
				
				//실제 저장할 파일 객체(옮길 파일)
				File up = new File(uploadFolder, rename);	//파일 이름이 변경되어 저장
				//up에 제대로된 파일을 원하는 장소레 저장할 수 있게 도와줄 것
				//임시 파일은 item에 담겨있고 실제 저장은 up에다가
				
				try {
					
					//임시파일을 실제 업로드 파일로 출력함
					item.write(up);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				uploadFile.setOriginName(item.getName());
				uploadFile.setStoredName(rename);
				
			}	//if(!item.isFormField())
			
		}	//while(iter.hasNext())
		
		System.out.println("FileService fileService() - paramData : " + paramData);
		System.out.println("FileService fileService() - uploadFile : " + uploadFile);
		
		//-----------------------------------------------------

		//8. DB에 최동 데이터 삽입하기
		Connection conn = JDBCTemplate.getConnection();
		
		int res = 0;
		
		//폼필드 데이터 삽입
		//Dao 만들어야해~
		
		
		//파일 업로드 처리 완료
		return true;
	}

}
