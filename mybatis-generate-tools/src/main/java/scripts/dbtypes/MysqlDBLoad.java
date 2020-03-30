package scripts.dbtypes;

import com.mnt.mybatis.generate.core.BaseDBLoadTemplate;
import com.mnt.mybatis.generate.model.db.JDBCInfo;
import com.mnt.mybatis.generate.utils.SqlExecuteUtils;
import com.mnt.mybatis.generate.vo.TableColumnVO;
import com.mnt.mybatis.generate.vo.TableNameVO;

import java.lang.reflect.Field;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * mysql db 表结构查询
 */
public class MysqlDBLoad extends BaseDBLoadTemplate {

//    public String getKey() {
//        return "mysql";
//    }
    /**
     * 获取数据库名称
     * @return
     */
    private String getDatabase(JDBCInfo jdbcInfo) {
        String result = null;
        try {
            DatabaseMetaData dmd = getConnection(jdbcInfo).getMetaData();
            Field f = dmd.getClass().getDeclaredField("database");
            f.setAccessible(true);
            result = (String) f.get(dmd);
        } catch (SQLException | NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return result;
    }


    @Override
    protected JDBCInfo getJdbcInfo() {
        JDBCInfo jdbcInfo = new JDBCInfo();
        jdbcInfo.setDbType("mysql");
        jdbcInfo.setDbDriver("com.mysql.cj.jdbc.Driver");
        return jdbcInfo;
    }

    @Override
    protected List<TableNameVO> listTableNameImpl(JDBCInfo jdbcInfo) {
        final List<TableNameVO> result = new ArrayList<>();
        String database = getDatabase(jdbcInfo);
        String sql = "select TABLE_NAME, TABLE_COMMENT from INFORMATION_SCHEMA.Tables where table_schema = '"+ database +"'";
        SqlExecuteUtils.query(getConnection(jdbcInfo), sql, (rs) -> {
            try {
                TableNameVO vo;
                while (rs.next()) {
                    vo = new TableNameVO();
                    vo.setTableName(rs.getString("TABLE_NAME"));
                    vo.setRemark(rs.getString("TABLE_COMMENT"));
                    result.add(vo);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        return result;
    }

    @Override
    protected List<TableColumnVO> listTableColumnImpl(JDBCInfo jdbcInfo, String tableName) {

        final List<TableColumnVO> result = new ArrayList<>();
        String database = getDatabase(jdbcInfo);
        String sql = "select COLUMN_NAME ,COLUMN_COMMENT as remark ,DATA_TYPE,character_maximum_length as max_len, numeric_precision as num_len from INFORMATION_SCHEMA.Columns where table_name = '" + tableName + "'  and table_schema='"+ database +"'";

        SqlExecuteUtils.query(getConnection(jdbcInfo), sql, (rs) -> {
            try {
                TableColumnVO vo;
                while (rs.next()) {
                    vo = new TableColumnVO();
                    vo.setCloumnName(rs.getString("column_name"));
                    vo.setCloumnType(rs.getString("data_type"));

                    Integer length;
                    if(rs.getObject("max_len") != null){
                        length = rs.getInt("max_len");
                    }
                    else {
                        if(rs.getObject("num_len") != null)
                        {
                            length = rs.getInt("num_len");
                        }
                        else
                        {
                            if(vo.getCloumnType().equals("timestamp"))
                            {
                                length = 6;
                            }
                            else
                            {
                                length = -1;
                            }
                        }
                    }
                    vo.setLength(length);
                    vo.setRemark(rs.getString("remark"));
                    result.add(vo);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        return result;
    }
}
