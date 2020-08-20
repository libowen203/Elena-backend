package com.elena.elena.dao;


import com.elena.elena.model.AbstractElenaNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Repository("sqliteDao")
public class SqliteElevationDao implements ElevationDao{

    private JdbcTemplate jdbcTemplate;

    @Autowired
    @Qualifier("httpDao")
    private ElevationDao httpDao;

    @Autowired
    public void init(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public int insert(Set<ElevationData> elevationData) {

        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("INSERT INTO elevation (ID, ELEVATION) values ");

        for(ElevationData data : elevationData){
            sqlBuilder.append("(")
                    .append(getParamSqlString(2))
                    .append(")")
                    .append(",");
        }
        String sql = sqlBuilder.deleteCharAt(sqlBuilder.length() - 1).toString();

        return this.jdbcTemplate.update(sql, (ps)->{
            int index = 1;
            for(ElevationData data : elevationData){
                ps.setString(index, data.getId());
                ps.setString(index + 1, String.valueOf(data.getElevation()));
                index += 2;
            }
        });
    }

    @Override
    public int delete(Set<ElevationData> elevationData) {
        return 0;
    }

    @Override
    public Collection<ElevationData> get(Set<ElevationData> elevationData) {

        Set<String> ids = new HashSet<>();
        for(ElevationData data : elevationData){
            ids.add(data.getId());
        }

        String sql = "SELECT id, elevation FROM elevation WHERE id in (" + getParamSqlString(ids.size()) + ")";

        List<ElevationData> retrievedData = this.jdbcTemplate.query(sql,
                (ps) -> {
                    int index = 1;
                    for(String id : ids){
                        ps.setString(index, id);
                        index++;
                    }
                },
                (rs, rowNum) ->
                new ElevationData(rs.getString("id"),
                        Float.valueOf(rs.getString("elevation"))));

        //All data are available in database, no need to fetch it from external source
        if(retrievedData.size() == elevationData.size()){
            return retrievedData;
        }

        for(ElevationData data : retrievedData){
            elevationData.remove(data);
        }

        Set<ElevationData> httpRetrievedData = new HashSet<>();
        httpRetrievedData.addAll(this.httpDao.get(elevationData));
        this.insert(httpRetrievedData);
        retrievedData.addAll(httpRetrievedData);

        return retrievedData;
    }

    @Override
    public int update(Set<ElevationData> elevationData) {
        return 0;
    }


    private  String getParamSqlString(int paramSize){
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < paramSize; i++){
            stringBuilder.append("?,");
        }

        return stringBuilder.deleteCharAt(stringBuilder.length() - 1).toString();
    }


    private String getCoordinate(AbstractElenaNode node){

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(node.getLatitude()).append(",").append(node.getLongitude());
        return stringBuilder.toString();
    }


    @Override
    public void close() throws IOException {
        httpDao.close();
    }
}
