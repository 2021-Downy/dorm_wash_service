<?php

    error_reporting(E_ALL);
    ini_set('display_errors',1);

    include('dbcon.php');


    $android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");


    if( (($_SERVER['REQUEST_METHOD'] == 'POST') && isset($_POST['submit'])) || $android )
    {

        // 안드로이드 코드의 postParameters 변수에 적어준 이름을 가지고 값을 전달 받습니다.

      $name = $_POST['name'];
	    $phone_num = $_POST['phone_num'];
	    $dorm_num = $_POST['dorm_num'];
	    $ID    = $_POST['ID'];

        if(!isset($errMSG)) // 모두 입력이 되었다면
        {
            try{
                // SQL문을 실행하여 데이터를 MySQL 서버의 USER 테이블에 저장합니다.
                $stmt = $con->prepare('UPDATE USER SET name=:name, dorm_num=:dorm_num, phone_num=:phone_num WHERE ID=:ID');
                $stmt->bindParam(':name', $name);
                $stmt->bindParam(':dorm_num', $dorm_num);
                $stmt->bindParam(':phone_num', $phone_num);
                $stmt->bindParam(':ID', $ID);

                if($stmt->execute())
                {
                    $successMSG = "정보 수정이 완료되었습니다.";
                }
                else
                {
                    $errMSG = "정보 수정에 실패하였습니다.";
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
                ID: <input type = "text" name = "ID" />
                name   : <input type = "text" name = "name" />
                dorm_num   : <input type = "text" name = "dorm_num" />
                phone_num   : <input type = "text" name = "phone_num" />
                <input type = "submit" name = "submit" />
            </form>

       </body>
    </html>

<?php
    }
?>
