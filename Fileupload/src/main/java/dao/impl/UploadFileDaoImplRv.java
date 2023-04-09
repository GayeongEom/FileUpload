package dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import common.JDBCTemplate;
import dao.face.UploadFileDaoRv;
import dto.UploadFile;

public class UploadFileDaoImplRv implements UploadFileDaoRv {
	
	private PreparedStatement ps = null;
	
	
	@Override
	public int insert(Connection conn, UploadFile uploadFile) {

		String sql = "";
		sql += "INSERT INTO uploadfile(fileno, origin_name, stored_name)";
		sql += " VALUES (uploadfile_seq.nextval, ?, ?)";
		
		int res = 0;
		
		try {
			ps = conn.prepareStatement(sql);
			
			ps.setString(1, uploadFile.getOriginName());
			ps.setString(2, uploadFile.getStoredName());
			
			res = ps.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCTemplate.close(ps);
		}
		
		return res;
	}

}
