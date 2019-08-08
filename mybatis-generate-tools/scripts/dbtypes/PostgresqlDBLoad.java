package dbtypes;

import com.mnt.mybatis.generate.core.BaseDBLoadTemplate;
import com.mnt.mybatis.generate.model.db.JDBCInfo;
import com.mnt.mybatis.generate.vo.TableColumnVO;
import com.mnt.mybatis.generate.vo.TableNameVO;

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


        return null;
    }

    @Override
    protected List<TableColumnVO> listTableColumnImpl(JDBCInfo jdbcInfo, String tableName) {
        return null;
    }


}
