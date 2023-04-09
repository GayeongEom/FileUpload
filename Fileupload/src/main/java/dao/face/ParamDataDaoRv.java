package dao.face;

import java.sql.Connection;

import dto.ParamData;

public interface ParamDataDaoRv {

	public int insert(Connection conn, ParamData paramData);

}
