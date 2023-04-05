package dao.face;

import java.sql.Connection;
import java.util.List;

import dto.UploadFile;

public interface UploadFileDao {

	/**
	 * 첨부 파일 정보 삽입하기
	 * 
	 * @param conn - DB 연결 객체
	 * @param uploadFile - 업로드된 파일의 정보
	 * @return 삽입 수행 결과(1 - 정상 삽입, 0 - 삽입 실패)
	 */
	public int insert(Connection conn, UploadFile uploadFile);

	/**
	 * 업로드된 파일 정보 조회하기
	 * 
	 * @param conn - DB 연결 객체
	 * @return 조회결과
	 */
	public List<UploadFile> select(Connection conn);

}
