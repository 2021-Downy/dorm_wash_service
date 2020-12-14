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
        
 
        if(!isset($errMSG)) // 모두 입력이 되었다면 
        {            
            
            try{
                $stmt = $con->prepare('UPDATE USER SET using_num = 0 where user_num = :user_num');
                $stmt->bindParam(':user_num', $user_num);
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
                $stmt = $con->prepare('UPDATE WashingMachine SET running = 0 where WM_num = :WM_num');
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

