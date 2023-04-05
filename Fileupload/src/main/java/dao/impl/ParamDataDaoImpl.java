package dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import common.JDBCTemplate;
import dao.face.ParamDataDao;
import dto.ParamData;

public class ParamDataDaoImpl implements ParamDataDao {
	
	private PreparedStatement ps = null;
	
	
	@Override
	public int insert(Connection conn, ParamData paramData) {

		String sql = "";
		sql += "INSERT INTO paramdata(datano, title, data1, data2)";
		sql += " VALUES (paramdata_seq.nextval, ?, ?, ?)";
		
		int  res = 0;
		
		try {
			ps = conn.prepareStatement(sql);
			
			ps.setString(1, paramData.getTitle());
			ps.setString(2, paramData.getData1());
			ps.setString(3, paramData.getData2());
			
			res = ps.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCTemplate.close(ps);
		}
		
		
		return res;
	}

}
