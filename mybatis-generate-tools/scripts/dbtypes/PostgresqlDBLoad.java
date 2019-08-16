package dbtypes;

import com.mnt.mybatis.generate.core.BaseDBLoadTemplate;
import com.mnt.mybatis.generate.model.db.JDBCInfo;
import com.mnt.mybatis.generate.utils.SqlExecuteUtils;
import com.mnt.mybatis.generate.vo.TableColumnVO;
import com.mnt.mybatis.generate.vo.TableNameVO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * postgres db 表结构查询
 */
public class PostgresqlDBLoad extends BaseDBLoadTemplate {

//    @Override
//    public String getKey() {
//        return "postgres";
//    }

    @Override
    protected JDBCInfo getJdbcInfo() {
        JDBCInfo jdbcInfo = new JDBCInfo();
        jdbcInfo.setDbType("postgres");
        jdbcInfo.setDbDriver("org.postgresql.Driver");
        return jdbcInfo;
    }

    @Override
    protected List<TableNameVO> listTableNameImpl(JDBCInfo jdbcInfo) {
        List<TableNameVO> result = new ArrayList<>();
        String sql = "select relname as table_name ,col_description(c.oid, 0) as remark from pg_class c where  relkind = 'r' and relname not like 'pg_%' and relname not like 'sql_%'";
        SqlExecuteUtils.query(getConnection(jdbcInfo), sql, (rs) -> {
            try {
                TableNameVO vo;
                while (rs.next()) {
                    vo = new TableNameVO();
                    vo.setTableName(rs.getString("table_name"));
                    vo.setRemark(rs.getString("remark"));
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

        String sql = "SELECT col_description(a.attrelid,a.attnum) as remark ,a.attname as cloumn_name , i.udt_name as data_type, i.character_maximum_length as max_len, i.numeric_precision as num_len FROM pg_class as c,pg_attribute as a, information_schema.columns as i where a.attname = i.column_name AND i.table_name = c.relname AND c.relname = '"
                + tableName + "' and a.attrelid = c.oid and a.attnum > 0 order by a.attnum";

        SqlExecuteUtils.query(getConnection(jdbcInfo), sql, (rs) -> {
            try {
                TableColumnVO vo;
                while (rs.next()) {
                    vo = new TableColumnVO();
                    vo.setCloumnName(rs.getString("cloumn_name"));
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
