package service.face;

import javax.servlet.http.HttpServletRequest;

public interface FileServiceRv {

	/**
	 * multipart/form-data 인코딩으로 전달된 요청 데이터를 처리
	 * DB에 전달데이터 정보를 저장
	 * 
	 * @param req - 요청 정보 객페
	 * @return 파일 업로드 처리 성공 / 실패 결과
	 */
	boolean fileupload(HttpServletRequest req);

}
