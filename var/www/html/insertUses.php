<?php

    error_reporting(E_ALL);
    ini_set('display_errors',1);

    include('dbcon.php');


    $android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");


    if( (($_SERVER['REQUEST_METHOD'] == 'POST') && isset($_POST['submit'])) || $android )
    {

        // 안드로이드 코드의 postParameters 변수에 적어준 이름을 가지고 값을 전달 받습니다.

        $user_num = $_POST['user_num'];
        $user_num = (int)$user_num;

  	    $WM_num    = $_POST['WM_num'];
  	    $WM_num  = (int)$WM_num;

  	    $date    = $_POST['date'];
  	    $start_time = $_POST['start_time'];
  	    $end_time = $_POST['end_time'];

        $left_time = $_POST['left_time'];


        if(!isset($errMSG)) // 모두 입력이 되었다면
        {
            try{
                // SQL문을 실행하여 데이터를 MySQL 서버의 USER 테이블에 저장합니다.
                $stmt = $con->prepare('INSERT INTO USES(user_num, WM_num, date, start_time, end_time, left_time)  VALUES(:user_num, :WM_num, :date, :start_time, :end_time, :left_time)');
                $stmt->bindParam(':user_num', $user_num);
        				$stmt->bindParam(':WM_num', $WM_num);
        				$stmt->bindParam(':date', $date);
        				$stmt->bindParam(':start_time', $start_time);
        				$stmt->bindParam(':end_time', $end_time);
        				$stmt->bindParam(':left_time', $left_time);

                if($stmt->execute())
                {
                    $successMSG = "사용기록을 추가했습니다.";
                }
                else
                {
                    $errMSG = "사용기록 추가 에러";
                }

            } catch(PDOException $e) {
                die("Database error: " . $e->getMessage());
            }

            try{
                $stmt = $con->prepare('UPDATE USER SET using_num = :WM_num where user_num = :user_num');
                $stmt->bindParam(':user_num', $user_num);
                $stmt->bindParam(':WM_num', $WM_num);

                if($stmt->execute())
                {
                    $successMSG = "사용자 정보를 수정했습니다.";
                }
                else
                {
                    $errMSG = "사용자 정보 수정 에러";
                }

            } catch(PDOException $e) {
                die("Database error: " . $e->getMessage());
            }


            try{
                $stmt = $con->prepare('UPDATE WashingMachine SET running = 1 where WM_num = :WM_num');
                $stmt->bindParam(':WM_num', $WM_num);
                if($stmt->execute())
                {
                    $successMSG = "세탁기 정보를 수정했습니다.";
                }
                else
                {
                    $errMSG = "세탁기 정보 수정 에러";
                }

            } catch(PDOException $e) {
                die("Database error: " . $e->getMessage());
            }


        }

    }

?>

<?php
    if (isset($errMSG)) echo $errMSG;
    if (isset($successMSG)) echo $successMSG;

	$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");

    if( !$android )
    {
?>
    <html>
       <body>

            <form action="<?php $_PHP_SELF ?>" method="POST">
                user_num : <input type = "int" name = "user_num" />
                WM_num : <input type = "int" name = "WM_num" />
                date : <input type = "text" name = "date" />
                start_time : <input type = "text" name = "start_time" />
                end_time : <input type = "text" name = "end_time" />
                left_time : <input type = "text" name = "left_time" />
                <input type = "submit" name = "submit" />
            </form>

       </body>
    </html>

<?php
    }
?>
