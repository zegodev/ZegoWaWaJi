webpackJsonp([1],[
/* 0 */
/***/ (function(module, exports, __webpack_require__) {

/*eslint-disable no-console */
__webpack_require__(1);

/***/ }),
/* 1 */
/***/ (function(module, exports) {

!function (window) {
    var n = document.documentElement,
        rootfont,
        isMobile = true,
        i = document.createElement('style');
    n.firstElementChild.appendChild(i);

    function infinite() {
        // var docW = document.documentElement.clientWidth;
        var docW = window.innerWidth;

        if (!navigator.userAgent.match(/Android|BlackBerry|iPhone|iPad|iPod|Opera Mini|IEMobile/i)) {
            isMobile = false;
            document.getElementsByTagName('html')[0].style.width = 375 + 'px';
            document.getElementsByTagName('html')[0].style.maxHeight = 690 + 'px';
        }
        if (isMobile) {
            if (docW < 320) {
                docW = 320;
                rootfont = 100 / 750 * docW;
                i.innerHTML = 'html{font-size:' + rootfont + 'px!important;}';
            } else if (docW <= 750) {
                rootfont = 100 / 750 * docW;
                i.innerHTML = 'html{font-size:' + rootfont + 'px!important;}';
            } else {
                i.innerHTML = 'html{font-size:100px!important;}';
            }
        } else {
            i.innerHTML = 'html{font-size:50px!important;}';
        }
    }
    window.addEventListener('resize', function () {
        infinite();
    }, !1);

    window.addEventListener('pageshow', function (e) {
        // pageshow无论这个页面是新打开的还是在往返缓存中的，都会在这个页面显示的时候触发。新打开的会在load后触发。
        // event对象中有一个persisted属性，是true时代表是从往返缓存中恢复的。
        // 缓存完全保存了整个页面，包括JS的执行状态，这就意味着不会再触发load事件。
        // 防止此情况发生，做一个判断，执行字体设置函数
        e.persisted && infinite();
    }, !1), infinite();
}(window);

/***/ }),
/* 2 */
/***/ (function(module, exports) {

// removed by extract-text-webpack-plugin

/***/ }),
/* 3 */
/***/ (function(module, exports) {

// removed by extract-text-webpack-plugin

/***/ }),
/* 4 */
/***/ (function(module, exports) {

// removed by extract-text-webpack-plugin

/***/ }),
/* 5 */
/***/ (function(module, exports) {

// removed by extract-text-webpack-plugin

/***/ }),
/* 6 */
/***/ (function(module, exports) {

// removed by extract-text-webpack-plugin

/***/ }),
/* 7 */
/***/ (function(module, exports) {

// removed by extract-text-webpack-plugin

/***/ }),
/* 8 */
/***/ (function(module, exports) {

// removed by extract-text-webpack-plugin

/***/ }),
/* 9 */
/***/ (function(module, exports) {

// removed by extract-text-webpack-plugin

/***/ }),
/* 10 */
/***/ (function(module, exports) {

// removed by extract-text-webpack-plugin

/***/ }),
/* 11 */
/***/ (function(module, exports) {

// removed by extract-text-webpack-plugin

/***/ }),
/* 12 */
/***/ (function(module, exports, __webpack_require__) {

/*global ZegoClient*/

// 工具函数
var util = __webpack_require__(17);

// dom元素

/**********************/

var $anchorWrapper = util.getById('anchor-wrapper'); // 主播/正在游戏的昵称盒子
var $anchor = util.getById('anchor'); // 主播/正在游戏的昵称


var $audience = util.getById('audience'); // 观众/房间人数


var $viewWrapper = util.getById('view-wrapper'); // 视频流视图盒子
var $frontView = util.getById('frontview'); // 正面视图
var $sideView = util.getById('sideview'); // 侧面视图


var $switchBtn = util.getById('switch-btn'); // 视图切换按钮


var $appointmentWrapper = util.getById('appointment-wrapper'); // 1.预约和取消预约按钮 以及 2.其下排队信息 的盒子
var $applyWrapper = util.getById('apply-wrapper'); // 预约和取消预约按钮的盒子
var $apply = util.getById('apply'); // 预约按钮
var $cancel = util.getById('cancel'); // 取消预约按钮
var $beforeQueue = util.getById('before-queue'); // 预约之前的显示块   当前排队人数 xxx 人
var $beforeQueueNum = util.getById('before-queueNum'); // 预约之前的显示块   当前排队人数 xxx 人  此处为xxx的人数数值
var $afterQueue = util.getById('after-queue'); // 预约成功之后的显示块   你已预约成功，当前排在第 xxx 位
var $afterQueueNum = util.getById('after-queueNum'); // 预约成功之后的显示块   你已预约成功，当前排在第 xxx 位，此处为xxx位数值


var $upornotWrapper = util.getById('upornot-wrapper'); // 上机或者不上机盒子
var $upornotCountDown = util.getById('upornot-count-down'); // 上机或者不上机盒子倒计时


var $operateWrapper = util.getById('operate-wrapper'); // 操作元素  1.上下左右按钮、2.抓取按钮的盒子
var $directWrapper = util.getById('direct-wrapper'); // 上下左右按钮的盒子
var $frontUp = util.getById('front-up'); // 正面 上按钮
var $sideUp = util.getById('side-up'); // 侧面 上按钮
var $frontDown = util.getById('front-down'); // 正面 下按钮
var $sideDown = util.getById('side-down'); // 侧面 下按钮
var $frontLeft = util.getById('front-left'); // 正面 左按钮
var $sideLeft = util.getById('side-left'); // 侧面 左按钮
var $frontRight = util.getById('front-right'); // 正面 右按钮
var $sideRight = util.getById('side-right'); // 侧面 右按钮
var $go = util.getById('go'); // 抓取按钮


var $countDownWrapper = util.getById('count-down-wrapper'); // 上机后的游戏倒计时盒子
var $countDown = util.getById('count-down'); // 上机后的游戏倒计时


var $upornotCancel = util.getById('upornot-cancel'); // 取消上机按钮
var $upornotConfirm = util.getById('upornot-confirm'); // 确认上机按钮


var $resultWrapper = util.getById('result-wrapper'); // 游戏结束界面盒子
var $back = util.getById('back'); // 游戏结束后的返回娃娃机按钮


// 游戏结束后，倒计时返回娃娃机
var $playAgainCountDown = util.getById('play-again-count-down'); // 再来一次按钮
var $playAgain = util.getById('play-again');

var $audio = util.getById('audio'); // 音效


var $logBtn = util.getById('log-btn'); // 日志
var $logViewer = util.getById('log-view');

/**********************/

var ENUM_STREAM_UPDATE_TYPE = { added: 0, deleted: 1 };

var showLog = false; //日志显示

var appid = 3265350344; //appid

var roomID = ""; //房间id
var idName = ""; //用户id
var nickName = ""; //用户昵称
var anchor_id = ""; //娃娃机主播id


// 链接websocket
var server = 'ws://wsliveroom' + appid + '-api.zego.im:8181/ws'; //wawaji接入服务器地址    --- 即构下发的server地址

var logUrl = ''; //log服务器地址          --- 可填可不填
var loginTokenUrl = 'http://wsliveroom' + appid + '-api.zego.im:8181/token'; //登录token派发地址       --- 业务后台自己的地址
var payTokenUrl = 'http://wsliveroom' + appid + '-api.zego.im:8181/pay'; //支付地址               --- 业务后台自己的地址


var zg; //zegoClient对象
var clientSeq = 1; //发送客户端请求seq
var serverSeq = 0; //发送服务端返回seq

var playingStatus = false; //是否游戏中状态
var playCountDownTime = 30; //游戏总时长
var upornotCountDownTime = 10; //上机确认超时时长
var countDownTimer; //超时timer

var isInitApply = true; // 是否是从预约按钮开始预约，还是从结果页开始自动预约

var loginToken = ""; //登录令牌
var payToken = ""; //支付令牌
var itemType = "itme_type1"; //商品类型
var itemPrice = 6; //商品价格

var payTimestamp = 0;

//获取用户id
var localIdName = util.getLocal('idName');
if (!localIdName) {
    idName = "" + new Date().getTime() + Math.floor(Math.random() * 100000);
    util.setLocal('idName', idName);
} else {
    idName = localIdName;
}
nickName = "u" + idName;

//获取房间id
roomID = window.location.search.slice(1).split('=')[1];
console.log('roomid = ', roomID, '\n');

// 文档加载完毕后执行
window.onload = function () {
    zg = window.zg = new ZegoClient();
    console.log('zg = ', zg);

    /*************************************/

    /*demo运行流程*/

    // 1.配置参数
    zg.config({
        appid: appid, // 必填，应用id
        idName: idName, // 必填，用户自定义id
        nickName: nickName, // 必填，用户自定义昵称
        server: server, // 必填，Websocket连接地址     
        logLevel: 1,
        logUrl: logUrl,
        remoteLogLevel: 0
    });

    /*************************************/

    // 2.  登录
    var videoVolumeList = [50, 0];
    var useLocalStreamList = []; // 本地流列表
    // （1）登录操作
    login();
    //先从客户服务器获得token，再使用token登录
    function login() {
        loadLoginToken();
    }
    // （2）获取登录token
    function loadLoginToken() {
        var xmlhttp;
        xmlhttp = new XMLHttpRequest();
        xmlhttp.onreadystatechange = function () {
            if (xmlhttp.readyState == 4) {
                if (xmlhttp.status == 200) {
                    console.log("login token succ:" + xmlhttp.responseText);
                    loginToken = xmlhttp.responseText;
                    doLogin();
                } else {
                    console.log("login token fail");
                    alert("获取登录信息失败");
                }
            }
        };
        xmlhttp.open("GET", loginTokenUrl + "?app_id=" + appid + "&id_name=" + idName, true);
        xmlhttp.send();
    }
    // （3）成功获取登录token后，执行登录操作
    function doLogin() {
        console.log("start doLogin");
        // 2.  登陆
        zg.login(roomID, 2, loginToken, function (streamList) {
            console.log('login sucess - data = ', streamList);
            // 缓存新的流
            useLocalStreamList = updateStreamInfo(streamList) || [];

            console.log("stream count:" + useLocalStreamList.length);
            // 登录成功后，马上主动发送获取游戏信息的命令
            getGameInfo();
        }, function (err) {
            console.log('login error - data = ', err);
        });
    }
    // （4）登录成功后，马上主动发送获取游戏信息的命令
    function getGameInfo() {
        console.log('获取游戏信息!');
        sendCustomCMD(++clientSeq, 518, operateData);
    }

    /*************************************/

    // 3、拉流

    function updateStreamInfo(streamList, updateType) {
        if (!streamList) {
            return;
        }

        var useStreamList = [];
        //取主播id
        if (streamList != null || streamList.length !== 0) {
            streamList.forEach(function (item) {
                // 我们这边的demo环境，一个房间中不止两条流，所以用这个方法做了筛选
                // 如果开发者那边的环境能保证一个房间中只有两条流，就可以不用筛选
                if (item.anchor_nick_name.indexOf("WWJS") === 0) {
                    anchor_id = item.anchor_id_name;
                    useStreamList.push(item);
                }
            });
        }
        console.log('useStreamList = ', useStreamList);

        if (useStreamList.length !== 0) {
            useStreamList.forEach(function (item) {
                // 设置两路视频分别对应的canvas渲染容器
                // 比如现在有两条流
                // stream_id分别为
                // r9sljdkfjslfjslkjfosdfksowsdjf这是我随便写的字符串ssfjklsdjfl_2
                // r9sljdkfjslfjslkjfosdfksowsdjf这是我随便写的字符串ssfjklsdjfl

                // 每一个stream_id对应一条流

                // 每一条流要对应一个canvas容器

                // 这里使用slice截取stream_id的最后2个字符串，判断是否是_2，
                // 如果是的话为正面流，并给他赋予正面canvas容器 $frontView
                // 如果不是的话为侧面面流，并给他赋予侧面canvas容器 $sideView
                if (item.stream_id.slice(-2) == '_2') {
                    item.videoView = $sideView;
                    item.videoVolume = videoVolumeList[1];
                } else {
                    item.videoView = $frontView;
                    item.videoVolume = videoVolumeList[0];
                }
            });

            var reUseStreamList = []; // 复用的流列表
            // 我们要先判定哪条流不存在了，哪条流复用了
            // 不存在的那条流，我们要调用停止拉流接口，释放canvas视图容器
            // 复用的流列表依旧不动

            // 这一步看起来有点多余，其实主要作用就是为了能够调用停止拉流接口，释放canvas视图，以便让后续那条新的流使用
            if (useLocalStreamList.length !== 0) {
                var reUseFlag = true;
                for (var k = 0; k < useStreamList.length; k++) {
                    reUseFlag = false;
                    for (var j = 0; j < useLocalStreamList.length; j++) {
                        // 判断登录成功后的流信息中的stream_id和本地的是否相等，相等的话则该流没有变化，存起来
                        if (useLocalStreamList[j].stream_id === useStreamList[k].stream_id) {
                            reUseStreamList.push(useStreamList[k].stream_id);
                            reUseFlag = true;
                            break;
                        }
                    }
                    // 服务端推过来的add类型的流，不做停止播流处理  -----   断线重连后，可能推来的流信息会发生变化，变化的本地流销毁 / 停止播放
                    if (updateType != ENUM_STREAM_UPDATE_TYPE.added && !reUseFlag) {
                        zg.stopPlayingStream(useLocalStreamList[j].stream_id);
                    }
                }
            }

            // 将成功登录成功后的流与复用的流进行对比
            // 如果stream_id相等，则表示该流不变，
            // 如果不等，表示是新的流，需要启动拉流接口，进行播放
            var playFlag = true;
            for (var m = 0; m < useStreamList.length; m++) {
                playFlag = false;
                for (var n = 0; n < reUseStreamList.length; n++) {
                    if (useStreamList[m] === reUseStreamList[n]) {
                        playFlag = true;
                        break;
                    }
                }
                if (!playFlag) {
                    // 不是重用的流可以重新设置播放，重用的不变

                    /* 若使用低延迟视频流播放方案不用设置该项  ----  sdk默认使用低延迟视频流播放
                    播放前可以使用该接口设置播放的视频流类型   ----  0: cdn（有延迟）  1 ：ultra（低延迟）  
                    zg.setPreferPlaySourceType(0);*/
                    // zg.startPlayingStream(useStreamList[m].stream_id, useStreamList[m].videoView);
                    console.log('调用startPlayingStream拉流, 流id 和 流视图分别为： ', useStreamList[m].stream_id, useStreamList[m].videoView);
                    zg.startPlayingStream(useStreamList[m].stream_id, useStreamList[m].videoView);
                    zg.setPlayVolume(useStreamList[m].stream_id, useStreamList[m].videoVolume);

                    // 该计时器仅供调试使用，帮助开发者了解是否有流地址存在，正式环境可删除掉
                    setTimeout(function () {
                        console.log('供播放的流地址 0 = ', zg.streamList[0] && zg.streamList[0].urls_ws);
                        console.log('供播放的流地址 1 = ', zg.streamList[1] && zg.streamList[1].urls_ws);
                    }, 1000);
                }
            }
        }
        return useStreamList;
    }

    // 视角切换状态
    var viewStatus = 0;
    // 视角切换
    $switchBtn.addEventListener('click', function (e) {
        playAudio();
        viewStatus = e.target.dataset['switch'];
        if (viewStatus === '1') {
            // 此时处于正面，切换为侧面
            e.target.dataset['switch'] = '0';
            util.removeClass($viewWrapper, 'front');
            util.removeClass($directWrapper, 'front');
        } else {
            // 此时处于侧面，切换为正面
            e.target.dataset['switch'] = '1';
            util.addClass($viewWrapper, 'front');
            util.addClass($directWrapper, 'front');
        }
    });

    /*************************************/

    // 4. 发送自定义消息
    var operateData = { // 默认回复信息
        "time_stamp": new Date().getTime()
    };
    var sessionID = 0; // 此次抓娃娃的sessionid， 后续指定都要带上 的sessionid

    // 抓取 按钮元素
    var directMapObj = {
        go: $go
    };
    // 抓取按钮 绑定的事件
    var directHandler = {
        go: gotocatch
    };

    // 阻止默认事件  阻止鼠标/手机长按屏幕 弹出菜单
    var preventObj = [$frontLeft, $sideLeft, $frontRight, $sideRight, $frontUp, $sideUp, $frontDown, $sideDown];
    for (var i = 0; i < preventObj.length; i++) {
        preventObj[i].addEventListener('contextmenu', function (e) {
            e.preventDefault();
        });
    }

    /************** 预约重试 *************/
    // 申请预约
    $apply.addEventListener('click', appointmentClientHandler);

    var appointmentTimer = null; // 预约重试计时器
    var tryAppointmentCount = 0; // 当前预约重试次数
    var tryAppointmentMaxCount = 5; // 最大预约重试次数
    // 循环尝试预约
    function appointmentClientHandler() {
        isInitApply = true; // 表示从点击预约按钮开始预约
        playAudio();
        tryAppointmentCount = 0;
        if (appointmentTimer) {
            clearInterval(appointmentTimer);
        }
        appointment();
        appointmentTimer = setInterval(function () {
            appointment();
        }, 2000);
    }
    // 申请预约
    function appointment() {
        tryAppointmentCount++;
        // 超过最大尝试预约次数，不再继续尝试预约
        if (tryAppointmentCount > tryAppointmentMaxCount) {
            clearInterval(appointmentTimer);
            tryAppointmentCount = 0;
            return;
        }
        console.log('申请预约!');
        var configData = {
            "time_stamp": new Date().getTime(),
            "continue": 0
        };
        sendCustomCMD(++clientSeq, 513, configData);
    }

    /************** 取消预约重试 *************/
    // 取消预约
    $cancel.addEventListener('click', cancelAppointmentClientHandler);

    var cancelAppointmentTimer = null; // 取消预约重试计时器
    var tryCancelAppointmentCount = 0; // 当前取消预约重试次数
    var tryCancelAppointmentMaxCount = 5; // 最大取消预约重试次数
    // 循环尝试取消预约
    function cancelAppointmentClientHandler() {
        playAudio();
        tryCancelAppointmentCount = 0;
        if (cancelAppointmentTimer) {
            clearInterval(cancelAppointmentTimer);
        }
        cancelAppointment();
        cancelAppointmentTimer = setInterval(function () {
            cancelAppointment();
        }, 2000);
    }
    // 取消预约
    function cancelAppointment() {
        playAudio();
        tryCancelAppointmentCount++;
        // 超过最大尝试预约次数，不再继续尝试预约
        if (tryCancelAppointmentCount > tryCancelAppointmentMaxCount) {
            clearInterval(cancelAppointmentTimer);
            tryCancelAppointmentCount = 0;
            return;
        }
        console.log('取消预约!');
        sendCustomCMD(++clientSeq, 514, operateData);
    }

    // 左移动
    function movetoleft(seq) {
        var curSeq = seq || ++clientSeq;
        playAudio();
        console.log('向左!');
        sendCustomCMD(curSeq, 528, operateData);
    }
    // 右移动
    function movetoright(seq) {
        var curSeq = seq || ++clientSeq;
        playAudio();
        console.log('向右!');
        sendCustomCMD(curSeq, 529, operateData);
    }
    // 前移动
    function movetoup(seq) {
        var curSeq = seq || ++clientSeq;
        playAudio();
        console.log('向前!');
        sendCustomCMD(curSeq, 531, operateData);
    }
    // 后移动
    function movetodown(seq) {
        var curSeq = seq || ++clientSeq;
        playAudio();
        console.log('向后!');
        sendCustomCMD(curSeq, 530, operateData);
    }
    // 停止移动
    function stopmove() {
        console.log('停止移动!');
        sendCustomCMD(++clientSeq, 533, operateData);
    }
    // 抓取娃娃
    function gotocatch() {
        playAudio();
        console.log('go!');

        //把支付获得的token一起发送给服务器验证
        var catchData = { "time_stamp": operateData.time_stamp, "pay_token": payToken };
        sendCustomCMD(++clientSeq, 532, catchData);
        clearInterval(countDownTimer);

        util.hideElement([$countDownWrapper]);
        util.addClass($directWrapper, 'disabled');
        tapParams.finish = true;
    }

    // 发送指令
    function sendCustomCMD(seq, cmd, data) {
        console.log('cmd = ', cmd, '  seq = ', seq);
        var custom_msg = assemblyMessage(seq, cmd, data);
        // 4、发送自定义消息/指令
        zg.sendCustomCommand([anchor_id], custom_msg, function (seq, custom_content) {
            console.log('sendCustomCommand 成功  success-content', JSON.parse(custom_content));
        }, function (err, seq, custom_content) {
            console.log('sendCustomCommand 失败  error-content', JSON.parse(custom_content));
        });
    }

    // 组装自定义消息
    function assemblyMessage(seq, cmd, data) {
        var custom_content = {
            "seq": seq,
            "cmd": cmd,
            "session_id": sessionID,
            "data": data
        };
        var custom_msg = {
            "request_id": idName + '-' + seq,
            "room_id": roomID,
            "from_userid": idName,
            "from_username": nickName,
            "custom_content": JSON.stringify(custom_content)
        };
        return JSON.stringify(custom_msg);
    }

    //长按实现
    var tapParams = {
        timer: null,
        element: {},
        tapStartTime: 0,
        type: 'increment'
    };

    window.clearTapTimer = function () {
        console.log('鼠标或者手指离开屏幕了!!!');
        clearInterval(tapParams.timer);
        stopmove();
        // PC端  移除 当鼠标或者手指离开屏幕时， 清除计时器 函数
        tapParams.element.removeEventListener('mouseup', window.clearTapTimer);
        tapParams.element.removeEventListener('mouseleave', window.clearTapTimer);

        /* 移动设备 */
        tapParams.element.removeEventListener('touchend', window.clearTapTimer);
        tapParams.element.removeEventListener('touchcencel', window.clearTapTimer);
    };

    window.tapEvent = function (aEvent, aType) {
        ++clientSeq;
        /* 阻止默认事件并解除冒泡 */
        aEvent.preventDefault();
        aEvent.stopPropagation();

        tapParams = {
            element: aEvent.target,
            startTime: new Date().getTime() / 1000,
            type: aType,
            finish: false
        };
        // PC端  注册 当鼠标或者手指离开屏幕时， 清除计时器 函数
        tapParams.element.addEventListener('mouseup', window.clearTapTimer);
        tapParams.element.addEventListener('mouseleave', window.clearTapTimer);

        /* 移动设备 */
        tapParams.element.addEventListener('touchend', window.clearTapTimer);
        tapParams.element.addEventListener('touchcencel', window.clearTapTimer);

        // 开始不停的发送移动指令
        window.changeMove();
        tapParams.timer = setInterval(window.changeMove, 1000);
    };

    // 开始不停的发送移动指令
    window.changeMove = function () {
        if (tapParams.finish) {
            window.clearTapTimer();
            return;
        }
        console.log("move info:", tapParams);
        if (tapParams.type == "front-left" || tapParams.type == "side-up") {
            movetoleft(clientSeq);
        } else if (tapParams.type == "side-left" || tapParams.type == "front-down") {
            movetodown(clientSeq);
        } else if (tapParams.type == "front-right" || tapParams.type == "side-down") {
            movetoright(clientSeq);
        } else if (tapParams.type == "side-right" || tapParams.type == "front-up") {
            movetoup(clientSeq);
        } else {
            console.log("其他情况");
        }
    };

    // 关闭成功/失败界面，返回娃娃机界面
    $back.addEventListener('click', function () {
        playAudio();
        util.removeClass($resultWrapper, 'success,fail');

        // 关闭再来一次的倒计时
        clearInterval(countDownTimer);

        // 关闭不断尝试从结果页去预约的计时器
        clearInterval(appointmentTimer);

        // 并且发送取消预约指令
        cancelAppointmentClientHandler();
    });

    /*************************************/

    // 5. 登出
    // zg.logout();


    /*************************************/

    // 6. 释放娃娃机房间所有资源
    // zg.release();


    /*************************************/

    // 事件

    // 服务端主动推过来的 custommessage 消息事件
    var operateStatus = false;
    var wwjPlayer = null; // 当前在玩的玩家
    var waitQueue = []; // 当前在排队人数
    var waitPosition = 0;
    var RECVCMD = {
        broadcast: 257,
        appointment: 272,
        cancelAppointment: 274,
        upSelect: 258,
        upSelectRsp: 273,
        operateResult: 260,
        gameInfo: 275
    };

    // 接收服务端主动推过来消息的接口
    zg.onRecvCustomCommand = function (from_userid, from_idName, custom_content) {
        // code 业务逻辑
        console.log('custom_content = ', custom_content);
        var recvCustomContent = {};
        // 服务端返回的消息可能解析失败
        try {
            recvCustomContent = JSON.parse(custom_content);
        } catch (e) {
            console.log('解析服务器返回的消息失败了！ = ', e);
        }
        console.log('客户端-onRecvCustomCommand = ', from_userid, from_idName, recvCustomContent);
        custom_content = JSON.parse(custom_content);
        // 516,517 需要seq   在data里面带服务端的返回的seq
        if (custom_content.cmd === RECVCMD.broadcast) {
            // 全员广播

            broadcastHandler(custom_content);
        } else if (custom_content.cmd === RECVCMD.appointment) {
            // 告知本次预约请求是否成功，预约成功则进入排队阶段，否则失败

            appointmentHandler(custom_content);
        } else if (custom_content.cmd === RECVCMD.cancelAppointment) {
            // 取消本次预约

            cancelAppointmentHandler();
        } else if (custom_content.cmd === RECVCMD.upSelect) {
            // 收到回应，是否要付费上机

            upSelectHandler(custom_content);
        } else if (custom_content.cmd === RECVCMD.upSelectRsp) {
            // 服务端返回的信息   对客户端发送的确认上机或者放弃玩游戏指令的回应

            upSelectRspHandler(custom_content);
        } else if (custom_content.cmd === RECVCMD.operateResult) {
            // 收到本次抓娃娃的结果

            operateResultHandler(custom_content);
        } else if (custom_content.cmd === RECVCMD.gameInfo) {
            // 收到本次游戏信息

            gameInfoHandler(custom_content);
        }
    };

    // 第一消息类
    // 处理收到的 全员广播消息  257 
    function broadcastHandler(custom_content) {
        $audience.innerHTML = custom_content.data.total;
        wwjPlayer = custom_content.data.player;
        // 若当前有人在玩，player不等于空
        if (JSON.stringify(wwjPlayer) === '{}' || wwjPlayer.name === '') {
            util.hideElement([$anchorWrapper]);
            $anchor.innerHTML = '';
        } else {
            util.showElement([$anchorWrapper]);
            $anchor.innerHTML = wwjPlayer.name;
        }
        waitQueue = custom_content.data.queue;
        // 排队队列不为空，则赋值设置当前的排队人数
        if (waitQueue.length !== 0) {
            $beforeQueueNum.innerHTML = waitQueue.length;
            for (var i = 0; i < waitQueue.length; i++) {
                if (waitQueue[i].id === idName) {
                    // 用户当前所处排列位置
                    waitPosition = i + 1;
                    break;
                }
            }
            $afterQueueNum.innerHTML = waitPosition;
        } else {
            $beforeQueueNum.innerHTML = 0;
        }
    }

    // 第二消息类
    // 处理收到的 告知本次预约请求是否成功，预约成功则进入排队阶段，否则失败  272 对应  513 请求预约
    function appointmentHandler(custom_content) {
        if (custom_content.data.result == 1) {
            // 失败
            alert('预约失败！');
            util.showElement([$apply]);
            util.hideElement([$cancel]);
            sessionID = 0;
        } else {
            // 成功
            if (isInitApply) {
                // 从点击预约按钮后，收到的回应
                console.log('从点击预约按钮后，收到的回应, 告知本次预约请求是否成功,  isInitApply = ', isInitApply);
                util.addClass($applyWrapper, 'disabled');

                waitPosition = custom_content.data.index;
                $afterQueueNum.innerHTML = waitPosition;
                util.hideElement([$apply, $beforeQueue]);
                util.showElement([$cancel, $afterQueue]);

                $afterQueueNum.innerHTML = custom_content.data.index;
            }
            sessionID = custom_content.data.session_id;
            util.setLocal('sessionID', sessionID);
        }

        // 收到预约回复，清除不断尝试预约的计时器
        clearInterval(appointmentTimer);
    }

    // 第三消息类
    // 处理收到的 取消本次预约  274 对应   514 取消预约
    function cancelAppointmentHandler() {
        // alert('取消预约成功');
        util.showElement([$apply, $beforeQueue]);
        util.hideElement([$cancel, $afterQueue]);

        // 收到取消预约回复，清除不断尝试取消预约的计时器
        clearInterval(cancelAppointmentTimer);
    }

    // 第四消息类
    // 处理收到的 收到回应，是否要付费上机，，--- 放弃 ？ 确定上机界面      258 排队轮到时，服务端自动发过来的， 客户端要回516包
    function upSelectHandler(custom_content) {
        serverSeq = custom_content.seq;
        var replyData = { "time_stamp'": new Date().getTime(), "seq": serverSeq };
        // 设置服务端返回的当次可以游戏的时间
        playCountDownTime = custom_content.data.game_time;
        sendCustomCMD(serverSeq, 516, replyData);

        if (isInitApply) {
            // 从点击预约按钮后，收到的回应
            console.log('从点击预约按钮后，收到的回应, 是否要付费上机,  isInitApply = ', isInitApply);

            util.showElement([$upornotWrapper]);
            // 重置是否上机的倒计时
            $upornotCountDown.innerHTML = upornotCountDownTime;
            countDown($upornotCountDown, upornotCountDownTime, function () {
                util.hideElement([$upornotWrapper]);
                confirmTocancel();
            });
        } else {
            // 结果页发出的预约，收到的回应
            console.log('结果页发出的预约收到的预约回应, isInitApply = ', isInitApply);
            util.removeClass($playAgain, 'disabled');
            // 注册上机事件
            $playAgain.addEventListener('click', pay);
            countDown($playAgainCountDown, upornotCountDownTime, function () {
                // 倒计时结束，用户还没有点击再来一次，则把按钮置为灰色不可用
                util.addClass($playAgain, 'disabled');
                $playAgain.removeEventListener('click', pay);
                $playAgainCountDown.innerHTML = 0;
            });
        }
    }

    // 根据第四消息类，做出的动作
    // 回复服务端是否上机 515  confirm 1 上机，  confirm 0放弃
    $upornotConfirm.addEventListener('click', pay); // 确定上机
    $upornotCancel.addEventListener('click', confimTocancelHandler); // 取消上机

    //支付  做了两件事  1、请求付款    2、付款成功则上机，否则失败
    function pay() {
        console.log('结果页发出的付款请求, isInitApply = ', isInitApply);
        if (!isInitApply) {
            util.addClass($playAgain, 'disabled');
        }
        var xmlhttp;
        xmlhttp = new XMLHttpRequest();
        xmlhttp.onreadystatechange = function () {
            if (xmlhttp.readyState == 4) {
                if (xmlhttp.status == 200) {
                    // 此处是要业务后台自己写判断逻辑，根据业务后台返回的信息来 断定是否支付成功
                    payToken = xmlhttp.responseText;
                    console.log('payToken = ', payToken);
                    upToPlayHandler();
                } else {
                    alert("请求支付失败");
                }
            }
        };
        // 此处的payTimestamp不止是请求付款的参数，也是发送上机指令的参数，必须要一样，否则娃娃机端验证失败
        payTimestamp = new Date().getTime();
        //实际情况，使用客户端自己的域名地址获得payToken
        xmlhttp.open("GET", payTokenUrl + "?app_id=" + appid + "&id_name=" + idName + "&session_id=" + sessionID + "&confirm=1" + "&time_stamp=" + payTimestamp + "&item_type=" + itemType + "&item_price=" + itemPrice, true);
        xmlhttp.send();
    }

    // 上机尝试
    var upToPlayTimer = null;
    var tryUpToPlayCount = 0;
    var tryUpToPlayMaxCount = 5;
    // 上机函数
    function upToPlayHandler() {
        if (!isInitApply) {
            // 从结果页点击再来一次，执行隐藏结果页
            util.removeClass($resultWrapper, 'success,fail');
        }
        playAudio();
        tryUpToPlayCount = 0;
        if (upToPlayTimer) {
            clearInterval(upToPlayTimer);
        }
        upToPlay();
        upToPlayTimer = setInterval(function () {
            upToPlay();
        }, 2000);
    }
    function upToPlay() {
        // 确定上机
        tryUpToPlayCount++;
        // 超过最大尝试预约次数，不再继续尝试预约
        if (tryUpToPlayCount > tryUpToPlayMaxCount) {
            clearInterval(upToPlayTimer);
            tryUpToPlayCount = 0;
            return;
        }
        console.log('确定上机');
        operateStatus = true;
        var replyData = { "confirm": 1, "time_stamp": payTimestamp, "config": payToken };
        sendCustomCMD(++clientSeq, 515, replyData);
    }

    // 放弃尝试
    var giveUpPlayTimer = null;
    var tryGiveUpPlayCount = 0;
    var tryGiveUpPlayMaxCount = 5;
    // 放弃函数
    function confimTocancelHandler() {
        playAudio();
        tryGiveUpPlayCount = 0;
        if (giveUpPlayTimer) {
            clearInterval(giveUpPlayTimer);
        }
        confirmTocancel();
        giveUpPlayTimer = setInterval(function () {
            confirmTocancel();
        }, 2000);
    }
    function confirmTocancel() {
        // 取消上机
        tryGiveUpPlayCount++;
        // 超过最大尝试预约次数，不再继续尝试预约
        if (tryGiveUpPlayCount > tryGiveUpPlayMaxCount) {
            clearInterval(giveUpPlayTimer);
            tryGiveUpPlayCount = 0;
            return;
        }
        console.log('取消上机');
        operateStatus = false;
        var replyData = { "confirm": 0, "time_stamp": new Date().getTime() };
        sendCustomCMD(++clientSeq, 515, replyData);
    }

    // 第五消息类
    // 处理收到的 服务端返回的信息   对客户端发送的确认上机或者放弃玩游戏指令的回应   273  对应   515 上机或放弃
    function upSelectRspHandler(custom_content) {
        var resultCode = custom_content.data.result;
        if (resultCode === 1) {
            console.log('发送确认上机的信息---格式无效！');
            return;
        } else if (resultCode === 2) {
            console.log('发送确认上机的信息---校验失败！');
            return;
        }
        console.log('operateStatus', operateStatus);
        if (operateStatus) {
            // alert('确认上机成功');
            util.showElement([$operateWrapper, $countDownWrapper]);
            util.hideElement([$appointmentWrapper, $upornotWrapper]);
            countDown($countDown, playCountDownTime, function () {
                gotocatch();
            });
            util.registerOperateHandler(directMapObj, directHandler);

            // 收到客户端发送的确认上机指令的服务端的回应，清除不断尝试确认上机的计时器
            clearInterval(upToPlayTimer);
        } else {
            // alert('取消上机成功');
            util.hideElement([$cancel, $afterQueue, $upornotWrapper]);
            util.showElement([$apply, $beforeQueue]);

            // 收到客户端发送的放弃玩游戏指令的服务端的回应，清除不断尝试放弃玩游戏的计时器
            clearInterval(giveUpPlayTimer);
            // 放弃成功后，清除是否上机的倒计时
            clearInterval(countDownTimer);
        }
        $applyWrapper.className = 'apply-wrapper';
    }

    // 第六消息类
    // 处理收到的 收到本次抓娃娃的结果
    function operateResultHandler(custom_content) {
        // 260
        if (JSON.stringify(custom_content.data.player) === '{}') {
            $apply.innerHTML = '预约抓娃娃';
            util.hideElement([$operateWrapper, $afterQueue]);
            util.showElement([$appointmentWrapper, $apply, $beforeQueue]);
        }
        if (custom_content.data.player.id !== idName) return;

        if (custom_content.data.result === 1) {
            // alert('恭喜您抓取到娃娃了！');
            util.removeClass($resultWrapper, 'fail');
            util.addClass($resultWrapper, 'success');
        } else {
            util.removeClass($resultWrapper, 'success');
            util.addClass($resultWrapper, 'fail');
            // alert('很遗憾，失败了！');
        }

        // 重置再玩一次秒数
        $playAgainCountDown.innerHTML = 10;
        util.hideElement([$operateWrapper, $cancel, $afterQueue]);
        util.showElement([$appointmentWrapper, $apply, $beforeQueue]);

        util.removeClass($directWrapper, 'disabled');
        $apply.innerHTML = '预约抓娃娃';

        // 不管成功或者失败，都移除方向操作和抓取动作的事件
        util.removeAllEventHandler(directMapObj, directHandler);

        // 回复结果
        console.log('发送继续玩指令！');
        var replyData = { "time_stamp": new Date().getTime(), continue: 1 };
        sendCustomCMD(++clientSeq, 517, replyData);
        isInitApply = false;

        // 发送继续玩的指令
        playAgainHandler();
    }

    // 第七消息类
    // 发送继续玩的指令
    function playAgainHandler() {
        tryAppointmentCount = 0;
        if (appointmentTimer) {
            clearInterval(appointmentTimer);
        }
        playAgain();
        appointmentTimer = setInterval(function () {
            playAgain();
        }, 2000);
    }

    function playAgain() {
        tryAppointmentCount++;
        // 超过最大尝试预约次数，不再继续尝试预约
        if (tryAppointmentCount > tryAppointmentMaxCount) {
            clearInterval(appointmentTimer);
            tryAppointmentCount = 0;
            return;
        }
        // 发送继续玩的指令
        console.log('发送继续玩指令！');
        var configData = {
            "time_stamp": new Date().getTime(),
            "continue": 1
        };
        sendCustomCMD(++clientSeq, 513, configData);
        isInitApply = false;
    }

    // 第八消息类
    // 获取到当前房间游戏信息，并设置房间人数，和当前排队人数
    function gameInfoHandler(custom_content) {
        // 设置当前排队人数，和房间总人数
        var gameInfo = custom_content.data;
        console.log('获取游戏信息= ', custom_content);
        $beforeQueueNum.innerHTML = gameInfo.queue.length;
        $audience.innerHTML = gameInfo.total;

        // 如果当前在游戏的主播id和本地的idName一样，则恢复游戏状态  -----  如正在游戏，刷新页面场景
        if (gameInfo.player.id === idName) {
            if (!playingStatus) {
                playingStatus = true;
                recoveGameStateHandler();
            }
        }
    }
    // 恢复游戏中状态处理
    function recoveGameStateHandler() {
        if (playingStatus) {
            util.showElement([$operateWrapper]);
            util.hideElement([$appointmentWrapper]);
            sessionID = util.getLocal('sessionID');
            util.registerOperateHandler(directMapObj, directHandler);
        }
    }

    // 接收房间IM消息
    zg.onRecvRoomMsg = function (chat_data, server_msg_id, ret_msg_id) {
        //    console.log(chat_data, server_msg_id, ret_msg_id);
    };

    // 服务端主动推过来的 连接断开事件
    zg.onDisconnect = function (err) {
        // code 业务逻辑
        console.log('客户端-onDisconnect = ', err);
        // alert('您断开连接了,请刷新页面！');
        // window.history.back();
    };

    // 服务端主动推过来的 用户被踢掉在线状态事件
    zg.onKickOut = function (err) {
        // code 业务逻辑
        console.log('客户端-onKickOut = ', err);
        // alert('您被踢下线了,请刷新页面！');
        // window.history.back();
    };

    // 服务端主动推过来的 流信息中的 ExtraInfo更新事件（暂时不用实现）
    zg.onStreamExtraInfoUpdated = function (streamList) {
        // code 业务逻辑
        // console.log('客户端-onStreamExtraInfoUpdated = ', streamList);
    };

    // 服务端主动推过来的 流的  创建/删除事件  updateType :“Added”||”Deleted”
    zg.onStreamUpdated = function (type, streamList) {
        // code 业务逻辑
        // console.log('客户端-onStreamUpdated = ', type, streamList);
        if (type == ENUM_STREAM_UPDATE_TYPE.added) {
            var tempStreamList;
            console.log("streamupdate add");
            tempStreamList = updateStreamInfo(streamList, ENUM_STREAM_UPDATE_TYPE.added);
            var useFlag = true,
                streamInfo;
            if (tempStreamList) {
                for (var i = tempStreamList.length - 1; i >= 0; i--) {
                    useFlag = false;
                    streamInfo = tempStreamList[i];
                    for (var j = 0; j < useLocalStreamList.length; j++) {
                        if (useLocalStreamList[j].stream_id === streamInfo.stream_id) {
                            useFlag = true;
                            break;
                        }
                    }

                    if (!useFlag) {
                        useLocalStreamList.push(streamInfo);
                    }
                }
            }
        } else if (type == ENUM_STREAM_UPDATE_TYPE.deleted) {
            console.log("streamupdate delete");
            if (useLocalStreamList) {
                deleteStreamInfo(streamList);
            }
        }
    };

    // 服务端推送过来已经删除掉的流   该函数删除本地流列表中对应的需要删除的流
    function deleteStreamInfo(streamList) {
        if (!streamList) {
            return;
        }

        var delStreamList = [];
        //取主播id
        if (streamList != null || streamList.length !== 0) {
            streamList.forEach(function (item) {
                if (item.anchor_nick_name.indexOf("WWJS") === 0) {
                    delStreamList.push(item);
                }
            });
        }
        console.log('delStreamList = ', delStreamList);

        if (delStreamList.length > 0) {
            for (var i = useLocalStreamList.length - 1; i >= 0; i--) {
                for (var j = 0; j < delStreamList.length; j++) {
                    if (useLocalStreamList[i].stream_id === delStreamList[j].stream_id) {
                        zg.stopPlayingStream(useLocalStreamList[i].stream_id);
                        useLocalStreamList.splice(i, 1);
                        break;
                    }
                }
            }
        }
    }

    // 服务端主动推过来的 流的播放状态, 视频播放状态通知，
    zg.onPlayStateUpdate = function (type, streamID) {
        // code 业务逻辑
        console.log('客户端-onPlayStateUpdate = ', type, streamID);
        if (type === 0) {
            for (var i = 0; i < useLocalStreamList.length; i++) {
                if (useLocalStreamList[i].stream_id == streamID) {
                    if (useLocalStreamList[i].videoView.nextElementSibling) {
                        useLocalStreamList[i].videoView.nextElementSibling.style.display = 'none';
                    }
                }
            }
        }
    };

    // 服务端主动推过来的 流的质量更新（暂时不用实现）
    zg.onPlayQualityUpdate = function (streamID, streamQuality) {
        // code 业务逻辑
        // console.log('客户端-onPlayQualityUpdate = ', streamID,  streamQuality);
    };

    // 日志显示
    $logBtn.addEventListener('click', function () {
        showLog = !showLog;
        logToggle();
    });
    document.addEventListener('click', function (e) {
        if (e.target.id != 'log-btn' && e.target.id != 'log-view' && showLog) {
            showLog = !showLog;
            logToggle();
        }
    });
    // 日志view切换
    function logToggle() {
        if (showLog) {
            if (zg.logger) {
                var logBefore = "";
                for (var i = 0; i < zg.logger.logCache.length; i++) {
                    logBefore = zg.logger.logCache[i] + "\n" + logBefore;
                }
                $logViewer.innerHTML = logBefore;
            }
            util.showElement([$logViewer]);
        } else {
            util.hideElement([$logViewer]);
        }
    }

    // 这个提示框是浏览器运行js发生错误时，弹框出来提示，方便移动端定位问题，如果出现弹框了，可以先自己定位监测下
    var errMaxCount = 5;
    var errCount = 0;
    window.onerror = function (msg, url, line, col, error) {
        errCount++;
        if (errCount <= errMaxCount) {
            //没有URL不上报！上报也不知道错误
            if (msg != "Script error." && !url) {
                return true;
            }
            //采用异步的方式
            //我遇到过在window.onunload进行ajax的堵塞上报
            //由于客户端强制关闭webview导致这次堵塞上报有Network Error
            //我猜测这里window.onerror的执行流在关闭前是必然执行的
            //而离开文章之后的上报对于业务来说是可丢失的
            //所以我把这里的执行流放到异步事件去执行
            //脚本的异常数降低了10倍
            setTimeout(function () {
                var data = {};
                //不一定所有浏览器都支持col参数
                col = col || window.event && window.event.errorCharacter || 0;

                data.url = url;
                data.line = line;
                data.col = col;
                if (!!error && !!error.stack) {
                    //如果浏览器有堆栈信息
                    //直接使用
                    data.msg = error.stack.toString();
                    if (data.msg.indexof("Cannot read property 'established' of null") > -1) {
                        return true;
                    }
                } else if (!!arguments.callee) {
                    //尝试通过callee拿堆栈信息
                    var ext = [];
                    var f = arguments.callee.caller,
                        c = 3;
                    //这里只拿三层堆栈信息
                    while (f && --c > 0) {
                        ext.push(f.toString());
                        if (f === f.caller) {
                            break; //如果有环
                        }
                        f = f.caller;
                    }
                    ext = ext.join(",");
                    data.msg = error.stack.toString();
                    if (data.msg.indexof("Cannot read property 'established' of null") > -1) {
                        return true;
                    }
                }
                //把data上报到后台！
                console.log('data = ', data);
                alert('发生错误 = ' + JSON.stringify(data) + ', 请把该错误截图，联系即构客服！');
            }, 0);
        }

        return true;
    };
};

// 工具函数

// 上机倒计时，游戏倒计时，再玩一次倒计时
function countDown(dom, countNum, cb) {
    console.log('dom = ', dom);
    if (countDownTimer) {
        clearInterval(countDownTimer);
    }
    var innerCountNum = countNum;
    dom.innerHTML = innerCountNum;
    countDownTimer = setInterval(function () {
        console.log('dom = ', dom);
        console.log('innerCountNum = ', innerCountNum);
        if (innerCountNum === 0) {
            clearInterval(countDownTimer);
            // 设置为原来的计数
            dom.innerHTML = countNum;
            if (cb) {
                cb();
            }
            return;
        }
        innerCountNum--;
        dom.innerHTML = innerCountNum;
    }, 1000);
}
// 音效
function playAudio() {
    setTimeout(function () {
        $audio.play();
    }, 400);
}

/***/ }),
/* 13 */,
/* 14 */,
/* 15 */,
/* 16 */,
/* 17 */
/***/ (function(module, exports) {

// 工具函数

// 通过id获取元素
function getById(selecotr) {
    return document.getElementById(selecotr);
}
// 移除类名
function removeClass(dom, className) {
    // 仿jq写法
    var rclass = /[\t\r\n\f]/g,
        j,
        clazz,
        curValue,
        cur;
    var removeList = className.split(',');
    curValue = dom.className;
    cur = (' ' + curValue + ' ').replace(rclass, ' ');
    if (cur) {
        j = 0;
        while (clazz = removeList[j++]) {
            while (cur.indexOf(' ' + clazz + ' ') > -1) {
                cur = cur.replace(' ' + clazz + ' ', ' ');
            }
        }
    }
    dom.className = cur;
}
// 添加类名
function addClass(dom, className) {
    var addList = className.split(',');
    addList.forEach(function (item) {
        dom.className += ' ' + item;
    });
}
// 隐藏元素
function hideElement(dom) {
    for (var i = 0; i < dom.length; i++) {
        dom[i].style.display = 'none';
    }
}
// 显示元素
function showElement(dom) {
    for (var i = 0; i < dom.length; i++) {
        dom[i].style.display = 'block';
    }
}
// 设置本地存储数据
function setLocal(key, value) {
    localStorage.setItem(key, value);
}
// 获取本地存储数据
function getLocal(key) {
    return localStorage.getItem(key);
}
// 注册事件
function registerOperateHandler(domMapObj, eventHandler) {
    console.log('注册了事件 = ', eventHandler);
    for (var el in eventHandler) {
        domMapObj[el].addEventListener('click', eventHandler[el]);
    }
}
// 移除事件
function removeAllEventHandler(domMapObj, eventHandler) {
    console.log('移除了事件 = ', eventHandler);
    for (var el in eventHandler) {
        domMapObj[el].removeEventListener('click', eventHandler[el]);
    }
}

module.exports = {
    getById: getById,
    removeClass: removeClass,
    addClass: addClass,
    hideElement: hideElement,
    showElement: showElement,
    setLocal: setLocal,
    getLocal: getLocal,
    registerOperateHandler: registerOperateHandler,
    removeAllEventHandler: removeAllEventHandler
};

/***/ }),
/* 18 */
/***/ (function(module, exports, __webpack_require__) {

/*global*/

// 左上角当前主播状态样式
__webpack_require__(2);

// 观众
__webpack_require__(4);

// 视频流视图样式
__webpack_require__(11);

// 视角切换按钮样式
__webpack_require__(9);

// 上下左右按钮样式
__webpack_require__(5);

// 抓取按钮样式
__webpack_require__(6);

// 预约按钮样式
__webpack_require__(3);

// 询问是否上机倒计时样式
__webpack_require__(10);

// 游戏结果样式
__webpack_require__(8);

// 日志样式
__webpack_require__(7);

// 响应式js
__webpack_require__(0);

// 主要逻辑
__webpack_require__(12);

/***/ })
],[18]);