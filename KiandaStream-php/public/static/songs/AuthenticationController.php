<?php

require_once 'Zend/Controller/Action.php';
require_once 'Zend/Session/Namespace.php';

class AuthenticationController extends Zend_Controller_Action {

    public function init() {
        $this->_helper->layout()->disableLayout();
        $this->_helper->viewRenderer->setNoRender(true);
        //  $this->session = Zend_Registry::get('sessionNamespace');
    }

    public function loginAction() {
        
        $objUserModel = Application_Model_Users::getInstance();
        $response = new stdClass();
        if ($this->getRequest()->isPost()) {

            $email = trim($this->getRequest()->getPost('email'));
            $password = trim($this->getRequest()->getPost('password'));
        //  echo "<pre>";print_r($password);echo "</pre>";die("cfh");
        }

        $authStatus = $objUserModel->checkAuthenticate($email, $password);
        //echo "<pre>";print_r($authStatus);echo "</pre>";


        if ($authStatus) {
//            $email = $authStatus['email'];
            @$response->data = 'Authentication successful';
            @$response->code = 200;
            echo json_encode(@$response);

//            echo "<pre>";print_r($result);echo "</pre>";die;
        } else {
            @$response->data = 'Incorrect Username or password';
            @$response->code = 197;

            echo json_encode(@$response);
        }
    }

    /* Name: JeyaKumar N
     * Date: 13/11/2014
     * Description: This function is for get data from users table to check user is already exits
     */

    public function getdataAction() {
        $objUserModel = Application_Model_Users::getInstance();
        $objUserMetaModel = Application_Model_UsersMeta::getInstance();
        $objImageGalleryModel = Application_Model_ImageGallery::getInstance();
        $objProfileViewerModel = Application_Model_ProfileViewer::getInstance();
        $objLikedYouModel = Application_Model_LikedYou::getInstance();
        $objBlockedModel = Application_Model_Blocked::getInstance();

        //die("test");
        if ($this->getRequest()->isPost()) {
            $fbId = $this->getRequest()->getPost('fbUserId');
        } else {
            $fbId = $this->getRequest()->getParam('fbUserId');
        }



//        $objSecuity = Engine_Vault_Security::getInstance();

        $authStatus = $objUserModel->getFbData($fbId);


        if ($authStatus) {
            $userid = $authStatus['userId'];
            $result = $objUserMetaModel->getUserMetaDetails($userid);
            //echo "<pre>";print_r();echo "</pre>";die;
            $allImgUrls = $objImageGalleryModel->getImageGalleryDetails($userid);
//            echo "<pre>";print_r($allImgUrls);echo "</pre>";die;
            $profileCount = $objProfileViewerModel->countViewers($userid);
            $likedCount = $objLikedYouModel->countLikedBy($userid);
            $blockedCount = $objBlockedModel->countBlockedUsers($userid);
            $superUser = $objUserModel->getUserDetails($userid);

            if ($allImgUrls == "") {
                $allImgUrls = "";
            }

            if ($authStatus['isActivated'] == 1) {
                $email = $authStatus['userEmail'];


                $status = $objUserModel->changeStatusOnline($email);
                @$response->data = 'Authentication successful';
                @$response->code = 200;
                @$response->visitorsCount = $profileCount;
                @$response->likesCount = $likedCount;
                @$response->blockedCount = $blockedCount;
                @$response->userdata = $result;
                @$response->superpower = $superUser['superPower'];
                @$response->balance = $superUser['balance'];
                @$response->role = $authStatus['Role'];
                @$response->imagegallery = $allImgUrls;
                @$response->profileimg = $superUser['userThumbnail'];


                echo json_encode(@$response);
                exit();
            } else {
                @$response->data = 'Account not verified';
                @$response->code = 198;
                @$response->userid = $authStatus['userId'];
                echo json_encode(@$response);
                exit();
            }
        } else {
            @$response->data = 'Incorrect Username or password';
            @$response->code = 197;

            echo json_encode(@$response);
            exit();
        }
    }

    public function getdataMailAction() {

        if ($this->getRequest()->post()) {
            $email = $this->getRequest()->getPost('userEmail');
        }
        $email = $this->getRequest()->getParam('userEmail');
//        echo $email;die;
        $objUserModel = Application_Model_Users::getInstance();
//        $objSecuity = Engine_Vault_Security::getInstance();

        $userResult = $objUserModel->getFbDataMail($email);

        if ($userResult) {
            echo json_encode($userResult);
        }
    }

    /* Name: Sam
     * Date: 19 Mar 2015
     * Description: Sign up conn for API
     */

    public function signupAction() {
        /*$response = new stdClass();
        $response->data = 'test API Calls';
        $response->code = 100; 
        echo json_encode($response);*/
        if ($this->getRequest()->isPost()) {
       //     echo"<pre>";print_r($this->getRequest()->getPost());echo"</pre>";die;
            /*$response = new stdClass();
        $response->data = 'POST';
        $response->code = 100; 
        echo json_encode($response);*/
            $response = new stdClass();
            $error = array();
            foreach ($this->getRequest()->getPost() as $key => $val) {
                if (strlen($val) > 0) {
                    $data[$key] = htmlspecialchars(trim($val));
                } else {
                    $error[$key] = $key . ' Can\'t be left blank.';
                    $response->data = $key . ' Can\'t be left blank.';
                    $response->code = 100;  // Set to check the code at the sender end as well
                    break;
                }
            }
              // echo"<pre>";print_r($data);echo"</pre>";die;
            if (count($error) > 0) {
                echo json_encode($response);
            } else {
                $email = $this->getRequest()->getPost('email');
                
                /*$response = new stdClass();
                $response->data = 'Data'.$email;
                $response->code = 100; 
                echo json_encode($response);*/
                $objUserModel = Application_Model_Users::getInstance();
//                $datas = array('email' => $email);
                //echo"<pre>";print_r($datas);echo"</pre>";die;
                $res = $objUserModel->isavailEmail($email);
            //    echo"<pre>";print_r($res);echo"</pre>";
                if($res){
                   // die("vjgbhk");
                    $response = new stdClass();
                    $response->data = 'try some other Email';
                    $response->code = 150;  //Error code for not availabale email address
                    echo json_encode($response);
                }
                    
                    //isavailUserId
//                    $username = $this->getRequest()->getPost('user_name');
////                    $datas = array('user_name' => $username);
//                    $res = $objUserModel->isavailUserId($username);
                // echo"<pre>";print_r($res);echo"</pre>";die("cfj");
//                    if($res){
//                        $response = new stdClass();
//                        $response->data = 'try some other UserName';
//                        $response->code = 155;  //Error code for not availabale UserName
//                        echo json_encode($response);
//                    }
                    else{
                       
                     // print_r($data);die("vghnc");
                        $res = $objUserModel->signUpUser($data);
                        if($res){
                            $res = "success";
                           echo json_encode($res);
                        }
                      
                    }
                }
                
            }
        }
    

}
