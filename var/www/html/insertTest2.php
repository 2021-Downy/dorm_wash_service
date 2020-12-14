<?php

    error_reporting(E_ALL);
    ini_set('display_errors',1);

    include('dbcon.php');


    $android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");


    if( (($_SERVER['REQUEST_METHOD'] == 'POST') && isset($_POST['submit'])) || $android )
    {

      // 안드로이드 코드의 postParameters 변수에 적어준 이름을 가지고 값을 전달 받습니다.

      $preferred_day = $_POST['preferred_day'];
      $preferred_time = $_POST['preferred_time'];
	    $user_num = $_POST['user_num'];

      // SQL문을 실행하여 데이터를 MySQL 서버의 USER 테이블에 저장합니다.
      $stmt = $con->prepare('INSERT INTO DATA_REPORT(preferred_day, preferred_time, user_num) VALUES(:preferred_day, :preferred_time, :user_num)');
      $stmt->bindParam(':preferred_day', $preferred_day);
      $stmt->bindParam(':preferred_time', $preferred_time);
      $stmt->bindParam(':user_num', $user_num);

      $stmt->execute()

    }

?>
