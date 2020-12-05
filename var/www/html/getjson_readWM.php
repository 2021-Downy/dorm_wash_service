<?php 

    error_reporting(E_ALL); 
    ini_set('display_errors',1); 

    include('dbcon.php');
    
    $android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");
    
    if( (($_SERVER['REQUEST_METHOD'] == 'POST') && isset($_POST['submit'])) || $android ){
	$dorm_num    = $_POST['dorm_num'];
	
    $stmt = $con->prepare('select * from WashingMachine where dorm_num=:dorm_num');
    $stmt->bindParam(':dorm_num', $dorm_num);
    $stmt->execute();
    }

    if ($stmt->rowCount() > 0)
    {
        $data = array(); 

        while($row=$stmt->fetch(PDO::FETCH_ASSOC))
        {
            extract($row);
    
            array_push($data, 
                array('WM_num'=>$WM_num,
                'position_row'=>$position_row,
                'position_column'=>$position_column,
                'running'=>$running
            ));
        }

        header('Content-Type: application/json; charset=utf8');
        $json = json_encode(array("webnautes"=>$data), JSON_PRETTY_PRINT+JSON_UNESCAPED_UNICODE);
        echo $json;
    }

?>
