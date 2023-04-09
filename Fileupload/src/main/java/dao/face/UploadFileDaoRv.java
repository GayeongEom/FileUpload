package dao.face;

import java.sql.Connection;

import dto.UploadFile;

public interface UploadFileDaoRv {

	public int insert(Connection conn, UploadFile uploadFile);

}
