package com.coursecloud.constants;

public interface URLConstants {
	public static final String SERVER_IP = "http://140.118.7.117/";
	public static final String COURSE_CLOUD_FOLDER = "course_cloud_html/";

	// PHPs
	public static final String URL_CHECK_MAC = SERVER_IP + "check_mac.php";
	public static final String URL_ADD_MEMBER = SERVER_IP + "addmember.php";
	public static final String URL_ADD_TEACHER = SERVER_IP
			+ COURSE_CLOUD_FOLDER + "add_teacher.php";
	public static final String URL_GET_MEMBER_LIST = SERVER_IP
			+ "get_attlist.php";
	public static final String URL_GET_SCORE_LIST = SERVER_IP
			+ "get_scorelist.php";
	public static final String URL_POST_SCORE = SERVER_IP + "rate_post.php";
	public static final String URL_GET_SCORE_RESULT = SERVER_IP
			+ COURSE_CLOUD_FOLDER + "rate_calculate.php";
	public static final String URL_GET_SCORE_COUNT = SERVER_IP
			+ COURSE_CLOUD_FOLDER + "check_count.php";
	public static final String URL_GET_SCORE_RECORD = SERVER_IP
			+ COURSE_CLOUD_FOLDER + "get_rate_record.php";
	public static final String URL_POST_PORTRAIT = SERVER_IP
			+ COURSE_CLOUD_FOLDER + "upload_image.php";
	public static final String URL_GET_MY_PORTRAIT = SERVER_IP
			+ COURSE_CLOUD_FOLDER + "get_my_portrait.php";
	public static final String URL_CHECK_IN = SERVER_IP + COURSE_CLOUD_FOLDER
			+ "checkin.php";
	public static final String URL_CHECK_IP = SERVER_IP + COURSE_CLOUD_FOLDER
			+ "checkip.php";

}
