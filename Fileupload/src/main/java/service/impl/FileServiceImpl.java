package service.impl;

import java.io.File;
import java.io.IOException;
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

import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;
import com.oreilly.servlet.multipart.FileRenamePolicy;

import common.JDBCTemplate;
import dao.face.ParamDataDao;
import dao.face.UploadFileDao;
import dao.impl.ParamDataDaoImpl;
import dao.impl.UploadFileDaoImpl;
import dto.ParamData;
import dto.UploadFile;
import service.face.FileService;

public class FileServiceImpl implements FileService {
	
	private ParamDataDao paramDataDao = new ParamDataDaoImpl();
	private UploadFileDao uploadFileDao = new UploadFileDaoImpl();

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
		
		
		//----------------------------------------------------------------------
		
				//7. 피싱된 전달 파라미터 데이터 처리하기
				
			      //   -> List<FileItem>객체에 파일과 폼필드 데이터가 들어있다
			      
			      //   -> 요청데이터(FileItem) 종류
			      
			      //      - 빈파일, 용량이 0인 파일
			      
			      
			      //      - 폼필드, form-data, 일반적인 전달 파라미터
			      //         전달된 데이터들을 DB에 INSERT한다
			      
			      //      - 파일 (크게 두가지 방법이 있다)
			      //         파일은 디스크에 저장
			      //         웹 서버의 로컬 폴더
			      //         파일의 정보는 DB에 INSERT한다
				
				//폼필드 전달 파라미터를 저장할 DTO객체
				ParamData paramData = new ParamData();
				
				//파일 정보를 저장할 DTO객체
				UploadFile uploadFile = new UploadFile();
				
				//파일아이템 리스트의 반복자
				Iterator<FileItem> iter = items.iterator();
				
				while(iter.hasNext()) {
					
					//전달 파라미터를 저장한 FileItem객체를 하나씩 꺼내서 적용하기
					FileItem item = iter.next();
					
					
					//----1. 빈 파일에 대한 처리 ----
					//비어있다면? 사이즈가 0이라는 뜻이겠쥐.
					//그럼 일단 그 사이즈를 알기 위해서는 if 문으로 getSize 를 해준다.
					
					if( item.getSize() <=0 ) {	//전달 데이터의 크기가 0이하
						//만약 0이하면, 다음 파일 처리를 해야하니까 if 문을 빠져나가서 다시 위에서부터 코드 시작해야한다.
						
						//continue를 이용해서 빈 파일을 무시하고 다음 FileItem 처리로 넘어간다.
						continue;
					}
					//----2. 폼 필드에 대한 처리 ----
					if(item.isFormField()) {
						
						//폼필드는 key=value 쌍으로 전달된다.
						
//						item.getFieldName(); //getFieldName은 key 값이다. 이 메소드로 얻어오는거임.
//						item.getString();	//getString은 value 값이다. 이 메소드로 얻어오는 거임. 
						//어떻게 쓰냐면, dto의 멤버필드에 맞춰서 넣어주면 된다~!
						
						//일단, 키(key)를 추출한다
						String key = item.getFieldName();
						
						//값(value)를 추출한다.
//						String value;
						//value 값을 null 로 초기화 시켜줘야한다.
						String value = null;
						try {
							//한글정보를 받아들이는 방법은 getString 방식으로 해야한다.
							//try catch 안에서 실행하면, 초기화 시키지 않고 나가면 쓰레기값이 그대로 있으니 (value 에_
							//꼭 try catch 밖에서 초기화를 시켜줘야 한다.
							
							value = item.getString("UTF-8");
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
						
						System.out.println("FileService fileupload() - key : " + key);
						System.out.println("FileService fileupload() - value : " + value);
					
						//전달파라미터의 name(key)에 맞게 DTO의 setter를 호출한다.
						if("title".equals(key)) {
							paramData.setTitle(value);
							
						} else if("data1".equals(key)) {
							paramData.setData1(value);
							
						} else if("data2".equals(key)) {
							paramData.setData2(value);
						}
					}
					
					//----3. 파일에 대한 처리 ----
					if( !item.isFormField()) {
						
						//업로드된 파일은 서버 로컬 HDD에 저장한다.
						//파일의 이름을 원본과 다르게 바꿔서 저장한다.
						
						//서버는 원본 파일 이름, 바꾼 파일 이름 둘 다 기억하고 있어야 한다.
						//	-> 그래서 DB에 원본 이름, 저장한 이름 모두 저장(INSERT)한다.
						
						//대부분의 사람들은 파일이름에 황지선_주민등록등본 이런식으로 하는편이 많다.
						//그럼 해커들이 해킹하고 싶어질거다.
						//그래서 서버에서 이름을 엄청 길게 만들어줘서 난독화 시켜줘서 보호를 해준다고 한다.
						//근디 우리는 그정도는 아니라고 한다..그래서 날짜 이름으로 문자열 변환을 해줄거라고 한다.
						
						//날짜 -> 문자열 변환 (java.util.Data -> String 변환)
						//	SimpleDataFormat 클래스 이용
						
						SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssS");
						String rename = sdf.format(new Date());	//현재시간
						//현재시간으로 하는 이유?
						//근데 엄청나게 많은 사람들이 동접으로 동시에 올리는 순간? 있을수 있다.
						//그러니 완벽한 코드는 아니다.
						//그냥 우리 수준에서 쓰는 코드라고 한다.
						//멀티쓰레딩을 하는 서비스 메소드가 저장되는 뭐시기를 공용자원으로 쓰기때문이다.
						//스레딩 자체의 문제라고 한다.
						//절대 겹치지 않는 다른 방법을 써야한다고 한다. 여러가지방법이 있다고 하는데
						//우리 수업에서는 안가르쳐주실 생각이신감...
						
						//그래서 지금 확인해볼수 있는건
						//getName을 해주면 name으로 가져온걸 오는데 null일 것이다. 근데 파일명이 불러져온다고 한다 . 뭔말이야!!!!
						System.out.println("FileService fileService() - 원본파일명 : " + item.getName());
						System.out.println("FileService fileService() - 저장파일명 : " + rename);
						
						
						//임시 보관하고 있는 파일(tmp폴더에) 실제 업로드 저장소로 옮기기
						
						//실제 파일 저장소
						File uploadFolder = new File( context.getRealPath("upload"));
						uploadFolder.mkdir();
						
						//실제 저장할 파일 객체 (옮길 파일)
						File up = new File(uploadFolder, rename);	//파일이름 변경되어 저장됨
						//up 에 제대로된 파일을 원하는 장소에 저장할 수 있게 도와줄 것이다.
						//임시파일은 item에 담겨있고, 실제 저장은 up에 다가.
						//그럼 임시파일을 up 에다가 어떻게 넣게? item.write(up) 을 쓰면 trycatch 퀵픽스가 뜬다.
						
						try {
							//임시파일을 실제 업로드 파일로 출력한다.
							item.write(up);
							
							
							
						} catch (Exception e) {
							e.printStackTrace();
						}
						
						uploadFile.setOriginName(item.getName());
						uploadFile.setStoredName(rename);
						
					} //if( !item.isFormField() ) end
					
				} //while(iter.hasNext()) end
				
				System.out.println("FileService fileService() - paramData : " + paramData);
				System.out.println("FileService fileService() - uploadFile : " + uploadFile);
				
				
				//----------------------------------------------------------------------

				//8. DB에 최종 데이터 삽입하기
				
				Connection conn = JDBCTemplate.getConnection();
				
				int res = 0;
				
				//폼필드 데이터 삽입
				res += paramDataDao.insert(conn, paramData);
				
				//파일 데이터 삽입
				res += uploadFileDao.insert(conn, uploadFile);
				
				if(res<2) {
					//두 인서트 중 하나라도 실패했을 경우
					JDBCTemplate.rollback(conn);
				} else {
					//두 인서트가 모두 성공했을 경우
					JDBCTemplate.commit(conn);
				}
				
		//파일 업로드 처리 완료
		return true;
	}
	
	
	//list()
	@Override
	public List<UploadFile> list() {

		Connection conn = JDBCTemplate.getConnection();
		
		List<UploadFile> list = uploadFileDao.select(conn);
		
		System.out.println("파일서비스쪽 리스트 result : " +  list);
		
		//이런 식으로도 가능
//		return uploadFileDao.select(JDBCTemplate.getConnection());
		return list;
	}
	
	
	@Override
	public boolean cosFileupload(HttpServletRequest req) {

		//multipart/form-data 형식의 요청데이터가 아닐 경우
		if(!ServletFileUpload.isMultipartContent(req)) {
			
			//파일 업로드 처리 중단
			return false;
		}
		
		
		//---- COS라이브러리를 이용한 파일 업로드 처리 ----
		
		//Multipart 객체의 생성자 매개변수 준비
		//	1. 요청 정보 객체 -> req
		
		
		//	2. 업로드된 파일이 저장될 경로
		String saveDirectory = req.getServletContext().getRealPath("upload");
		
		File directory = new File(saveDirectory);
		directory.mkdir();
		
		
		//	3. 업로드 용량 제한 크기
		int maxPostSize = 10 * 1024 * 1024;	//10MB
		
		
		//	4. 인코딩 설정(한글 UTF-8)
		String encoding = "UTF-8";
		
		
		//	5. 중복될 파일이름을 처리하는 정책
		FileRenamePolicy policy = new DefaultFileRenamePolicy();
		
		//-----------------------------------------------------------------------
		
		MultipartRequest mul = null;
		
		try {
			mul = new MultipartRequest(req, saveDirectory, maxPostSize, encoding, policy);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//-----------------------------------------------------------------------
		
		//폼필드 전달 파라미터 정보
		
//		String title = req.getParameter("title");
//		String username = req.getParameter("username");
//		String fruit = req.getParameter("fruit");

		String title = mul.getParameter("title");
		String username = mul.getParameter("username");
		String fruit = mul.getParameter("fruit");
		
		System.out.println("fileService cosFileupload() - title : " + title);
		System.out.println("fileService cosFileupload() - username : " + username);
		System.out.println("fileService cosFileupload() - fruit : " + fruit);
		
		ParamData paramdata = new ParamData(0, title, username, fruit);
		Connection conn = JDBCTemplate.getConnection();
		paramDataDao.insert(conn, paramdata);
		JDBCTemplate.commit(conn);
		
		//--------------------------------------------------------------------------------
		
		//파일 전달파라미터 처리
		
		//원본 파일 이름
		String origin = mul.getOriginalFileName("upfile");
		
		//저장된 파일 이름
		String stored = mul.getFilesystemName("upfile");
		
		//업로드 파일 정보 DTO객체
		UploadFile uploadFile = new UploadFile(0, origin, stored);
		
		//--------------------------------------------------------------------------------

		//DB처리
		int res = uploadFileDao.insert(conn, uploadFile);
		if(res>0) {
			JDBCTemplate.commit(conn);
		} else {
			JDBCTemplate.rollback(conn);
		}
		
		//--------------------------------------------------------------------------------
		
		//파일 업로드 성공
		return true;
	}
	
}
