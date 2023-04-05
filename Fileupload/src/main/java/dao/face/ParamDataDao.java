package dao.face;

import java.sql.Connection;

import dto.ParamData;

public interface ParamDataDao {

	/**
	 * 폼필드(form-data)전달파라미터 정보를 삽입
	 * 
	 * @param conn - DB 연결 객체
	 * @param paramData - DB에 저장할 전달파라미터 정보
	 * @return 삽입수행결과(1 - 정상 삽입, 0 - 삽입 실패)
	 */
	public int insert(Connection conn, ParamData paramData);

}
