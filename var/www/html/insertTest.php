<?php 
 
    error_reporting(E_ALL); 
    ini_set('display_errors',1); 
 
    include('dbcon.php');
 
 
    $android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");
 
 
    if( (($_SERVER['REQUEST_METHOD'] == 'POST') && isset($_POST['submit'])) || $android )
    {
 
        // 안드로이드 코드의 postParameters 변수에 적어준 이름을 가지고 값을 전달 받습니다.
 
        $name = $_POST['name'];
	    $ID    = $_POST['ID'];
	    $pw = $_POST['pw'];
	    $phone_num    = $_POST['phone_num'];
	    $dorm_num = $_POST['dorm_num'];
        
        if(empty($name)){
            $errMSG = "이름을 입력하세요.";
        }
        else if(empty($ID)){
            $errMSG = "아이디를 입력하세요.";
        }
        else if(empty($pw)){
            $errMSG = "비밀번호를 입력하세요.";
        }
        else if(empty($phone_num)){
            $errMSG = "연락처를 입력하세요.";
        }
        else if(empty($dorm_num)){
            $errMSG = "기숙사를 입력하세요.";
        }
 
        if(!isset($errMSG)) // 모두 입력이 되었다면 
        {
            try{
                // SQL문을 실행하여 데이터를 MySQL 서버의 USER 테이블에 저장합니다. 
                $stmt = $con->prepare('INSERT INTO USER(ID, pw, name, dorm_num, phone_num, using_num)  VALUES(:ID, :pw, :name, :dorm_num, :phone_num, 0)');
                $stmt->bindParam(':ID', $ID);
				$stmt->bindParam(':pw', $pw);
				$stmt->bindParam(':name', $name);
				$stmt->bindParam(':dorm_num', $dorm_num);
				$stmt->bindParam(':phone_num', $phone_num);
 
                if($stmt->execute())
                {
                    $successMSG = "새로운 사용자를 추가했습니다.";
                }
                else
                {
                    $errMSG = "사용자 추가 에러";
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
                pw   : <input type = "text" name = "pw" />
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

