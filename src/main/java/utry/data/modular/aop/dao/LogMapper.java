package utry.data.modular.aop.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import utry.data.modular.aop.SongXiaLog;

@Mapper
public interface LogMapper {

    void insertSongXiaLog(@Param("dto") SongXiaLog songXiaLog);
}
