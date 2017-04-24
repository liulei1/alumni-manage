package cn.blaze.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import cn.blaze.domain.StudentInfo;

@Repository
public interface StudentInfoDao {

	/**
	 * 插入学生信息,包括主键
	 * @Title insertStudentInfo
	 * @Description：
	 * @param studentInfo
	 * @user LiuLei 2017年4月23日
	 * @updater：
	 * @updateTime：
	 */
	void insertStudentInfo(StudentInfo studentInfo);
	
	/**
	 * 插入学生信息,主键自动生成
	 * @Title insertStudentInfoWithIdAuto
	 * @Description：
	 * @param studentInfo
	 * @user LiuLei 2017年4月23日
	 * @updater：
	 * @updateTime：
	 */
	void insertStudentInfoWithIdAuto(StudentInfo studentInfo);
	
	/**
	 * @Title selectByPara
	 * @Description：条件查询
	 * @param map
	 * @return
	 * @user LiuLei 2017年4月24日
	 * @updater：
	 * @updateTime：
	 */
	List<StudentInfo> selectByPara(Map<String, Object> map);

	/**
	 * @Title selectMapByPara
	 * @Description：条件查询学生信息,结果是学生信息对应map的列表
	 * @param map 条件
	 * @return
	 * @user LiuLei 2017年4月24日
	 * @updater：
	 * @updateTime：
	 */
	List<Map<String, Object>> selectMapByPara(Map<String, Object> map);

	/**
	 * @Title selectById
	 * @Description：根据Id查询
	 * @param studentId
	 * @return
	 * @user LiuLei 2017年4月24日
	 * @updater：
	 * @updateTime：
	 */
	StudentInfo selectById(@Param("id")String studentId);

	/**
	 * @Title updateById
	 * @Description：根据Id更新学生信息
	 * @param studentInfo
	 * @user LiuLei 2017年4月24日
	 * @updater：
	 * @updateTime：
	 */
	void updateById(StudentInfo studentInfo);

}
