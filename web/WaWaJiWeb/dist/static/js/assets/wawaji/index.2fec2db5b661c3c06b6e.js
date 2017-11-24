webpackJsonp([0],[
/* 0 */,
/* 1 */,
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
/***/ (function(module, exports) {

module.exports = window.jQuery;

/***/ }),
/* 13 */,
/* 14 */,
/* 15 */
/***/ (function(module, exports, __webpack_require__) {

/* WEBPACK VAR INJECTION */(function($) {
__webpack_require__(2);

__webpack_require__(11);

__webpack_require__(9);

__webpack_require__(5);

__webpack_require__(6);

__webpack_require__(3);

__webpack_require__(10);

__webpack_require__(7);

__webpack_require__(4);

__webpack_require__(8);

__webpack_require__(0);

var ENUM_STREAM_UPDATE_TYPE = { added: 0, deleted: 1 };

var errNum = 0;

var server = 'ws://106.15.41.49:8181/ws';
var logUrl = 'ws://106.15.41.49:8181/log';
var loginTokenUrl = 'http://106.15.41.49:8181/token';
var payTokenUrl = 'http://106.15.41.49:8181/pay';

var ls = localStorage;
var showLog = false;

var appid = 3177435262;
var roomID = "";
var idName = "";
var nickName = "";
var anchor_id = "";

var zg;
var seq = 1;
var custom_seq = 1;
var playingStatus = false;
var playCountDownTime = 30;
var upornotCountDownTime = 10;
var countDownTimer;

var loginToken = "";
var payToken = "";
var itemType = "itme_type1";
var itemPrice = 6;
var localIdName = getLocal('idName');
if (!localIdName) {
    idName = "" + new Date().getTime() + Math.floor(Math.random() * 100000);
    setLocal('idName', idName);
} else {
    idName = localIdName;
}
nickName = "u" + idName;
errNum++;

roomID = window.location.search.slice(1).split('=')[1];
console.log('roomid', roomID, '\n');
errNum++;

var $anchorWrapper = getById('anchor-wrapper');

var $anchor = getById('anchor');

var $audience = getById('audience');

var $viewWrapper = getById('view-wrapper');

var $frontView = getById('frontview');

var $sideView = getById('sideview');

var $switchBtn = getById('switch-btn');

var $appointmentWrapper = getById('appointment-wrapper');

var $applyWrapper = getById('apply-wrapper');

var $apply = getById('apply');

var $cancel = getById('cancel');

var $beforeQueue = getById('before-queue');

var $beforeQueueNum = getById('before-queueNum');

var $afterQueue = getById('after-queue');

var $afterQueueNum = getById('after-queueNum');

var $upornotWrapper = getById('upornot-wrapper');

var $upornotCountDown = getById('upornot-count-down');

var $operateWrapper = getById('operate-wrapper');

var $directWrapper = getById('direct-wrapper');

var $frontUp = getById('front-up');

var $sideUp = getById('side-up');

var $frontDown = getById('front-down');

var $sideDown = getById('side-down');

var $frontLeft = getById('front-left');

var $sideLeft = getById('side-left');

var $frontRight = getById('front-right');

var $sideRight = getById('side-right');

var $go = getById('go');

var $countDownWrapper = getById('count-down-wrapper');

var $countDown = getById('count-down');

var $upornotCancel = getById('upornot-cancel');

var $upornotConfirm = getById('upornot-confirm');

var $resultWrapper = getById('result-wrapper');

var $back = getById('back');

var $backCountDown = getById('back-count-down');

var $audio = getById('audio');

var $logBtn = getById('log-btn');
var $logViewer = getById('log-view');

function getById(selecotr) {
    return document.getElementById(selecotr);
}
function hideElement(dom) {
    for (var i = 0; i < dom.length; i++) {
        dom[i].style.display = 'none';
    }
}
function showElement(dom) {
    for (var i = 0; i < dom.length; i++) {
        dom[i].style.display = 'block';
    }
}
function countDown(dom, countNum, cb) {
    if (countDownTimer) {
        clearInterval(countDownTimer);
    }
    var innerCountNum = countNum;
    countDownTimer = setInterval(function () {
        if (innerCountNum === 0) {
            clearInterval(countDownTimer);
            if (cb) {
                cb();
            }

            dom.innerHTML = countNum;
            return;
        }
        innerCountNum--;
        dom.innerHTML = innerCountNum;
    }, 1000);
}
function setLocal(key, value) {
    ls.setItem(key, value);
}
function getLocal(key) {
    return ls.getItem(key);
}

function registerOperateHandler(directMapObj, directHandler) {
    for (var el in directHandler) {
        directMapObj[el].addEventListener('click', directHandler[el]);
    }
}

function removeAllEventHandler(directMapObj, directHandler) {
    for (var el in directHandler) {
        directMapObj[el].removeEventListener('click', directHandler[el]);
    }
}

window.onload = function () {
    zg = window.zg = new ZegoClient();

    var viewStatus = 0;
    var useStreamList = [];

    errNum++;

    $switchBtn.addEventListener('click', function (e) {
        $audio.play();
        viewStatus = e.target.dataset['switch'];
        if (viewStatus === '1') {
            e.target.dataset['switch'] = '0';
            $viewWrapper.className = $viewWrapper.className.replace(/front/g, '');
            $directWrapper.className = $directWrapper.className.replace(/front/g, '');
        } else {
            e.target.dataset['switch'] = '1';
            $directWrapper.className += ' front';
            $viewWrapper.className += ' front';
        }
    });

    var directMapObj = {
        frontLeft: $frontLeft,
        sideLeft: $sideLeft,
        frontRight: $frontRight,
        sideRight: $sideRight,
        frontUp: $frontUp,
        sideUp: $sideUp,
        frontDown: $frontDown,
        sideDown: $sideDown,
        go: $go
    };

    var directHandler = {
        frontLeft: movetoleft,
        sideLeft: movetodown,
        frontRight: movetoright,
        sideRight: movetoup,
        frontUp: movetoup,
        sideUp: movetoleft,
        frontDown: movetodown,
        sideDown: movetoright,
        go: gotocatch
    };

    var preventObj = [$frontLeft, $sideLeft, $frontRight, $sideRight, $frontUp, $sideUp, $frontDown, $sideDown];
    for (var i = 0; i < preventObj.length; i++) {
        preventObj[i].addEventListener('contextmenu', function (e) {
            e.preventDefault();
        });
    }

    $apply.addEventListener('click', appointment);

    $cancel.addEventListener('click', cancelAppointment);

    $back.addEventListener('click', function () {
        $audio.play();
        $resultWrapper.className = $resultWrapper.className.replace(/(success|fail)/g, '');
    });

    $logBtn.addEventListener('click', function () {
        showLog = !showLog;
        if (showLog) {
            if (zg.logger) {
                var logBefore = "";
                for (var i = 0; i < zg.logger.logCache.length; i++) {
                    logBefore = zg.logger.logCache[i] + "\n" + logBefore;
                }
                $logViewer.innerHTML = logBefore;
            }
            $logViewer.style.display = 'block';
        } else {
            $logViewer.style.display = 'none';
        }
    });

    function login() {

        loadLoginToken();
    }

    function loadLoginToken() {
        var xmlhttp;
        if (window.XMLHttpRequest) {
            xmlhttp = new XMLHttpRequest();
        } else {
            xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
        }
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

    function doLogin() {
        console.log("start doLogin");
        zg.login(roomID, 0, loginToken, function (streamList) {
            console.log('login sucess - data = ', streamList);
            useStreamList = updateStreamInfo(streamList);

            useLocalStreamList = useStreamList || [];
            console.log("stream count:" + useLocalStreamList.length);
        }, function (err) {
            console.log('login error - data = ', err);
        });
    }

    function updateStreamInfo(streamList) {

        if (!streamList) {
            return;
        }

        var useStreamList = [];

        if (streamList != null || streamList.length !== 0) {
            streamList.forEach(function (item) {
                if (item.anchor_nick_name.indexOf("WWJS") === 0) {
                    anchor_id = item.anchor_id_name;
                    useStreamList.push(item);
                }
            });
        }
        console.log('useStreamList = ', useStreamList);

        if (useStreamList.length !== 0) {
            useStreamList.forEach(function (item, index) {
                if (item.stream_id.slice(-2) == '_2') {
                    item.videoView = $frontView;
                } else {
                    item.videoView = $sideView;
                }
                item.videoVolume = videoVolumeList[index];
                if (item.extra_info) {
                    $beforeQueueNum.innerHTML = JSON.parse(item.extra_info).queue_number;
                    $audience.innerHTML = JSON.parse(item.extra_info).total;
                }
                if (item.extra_info && JSON.parse(item.extra_info).player.id === idName) {
                    if (!playingStatus) {
                        playingStatus = true;
                        RecoveGameStateHandler();
                    }
                }
            });

            var reUseStreamList = [];
            if (useLocalStreamList.length !== 0) {
                var reUseFlag = true;
                for (var j = 0; j < useLocalStreamList.length; j++) {
                    reUseFlag = false;
                    for (var k = 0; k < useStreamList.length; k++) {
                        if (useLocalStreamList[j].stream_id === useStreamList[k].stream_id) {
                            reUseStreamList.push(useStreamList[k].stream_id);
                            reUseFlag = true;
                            break;
                        }
                    }

                    if (!reUseFlag) {
                        zg.stopPlayingStream(useLocalStreamList[j].stream_id);
                    }
                }
            }

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
                    zg.startPlayingStream(useStreamList[m].stream_id, useStreamList[m].videoView);
                    zg.setPlayVolume(useStreamList[m].stream_id, useStreamList[m].videoVolume);
                }
            }
        }

        return useStreamList;
    }

    function deleteStreamInfo(streamList) {
        if (!streamList) {
            return;
        }

        var delStreamList = [];

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

    function pay() {
        var xmlhttp;
        if (window.XMLHttpRequest) {
            xmlhttp = new XMLHttpRequest();
        } else {
            xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
        }
        xmlhttp.onreadystatechange = function () {
            if (xmlhttp.readyState == 4) {
                if (xmlhttp.status == 200) {
                    payToken = xmlhttp.responseText;
                    upToPlay();
                } else {
                    alert("支付失败");
                }
            }
        };

        xmlhttp.open("GET", payTokenUrl + "?app_id=" + appid + "&id_name=" + idName + "&item_type=" + itemType + "&item_price=" + itemPrice, true);
        xmlhttp.send();
    }

    zg.config({
        appid: appid,
        idName: idName,
        nickName: nickName,
        server: server,
        logLevel: 1,
        logUrl: logUrl
    });

    var videoVolumeList = [0, 50];
    var useLocalStreamList = [];
    login();

    var operateData = {
        "time_stamp": new Date().getTime()
    };

    function sendCustomCMD(cmd, data) {
        var custom_msg = assemblyMessage(cmd, data);
        zg.sendCustomCommand([anchor_id], custom_msg, function (seq, custom_content) {
            console.log('sendcustomCMD 成功  success-content', JSON.parse(custom_content));
        }, function (err, seq, custom_content) {
            console.log('sendcustomCMD 失败  error-content', JSON.parse(custom_content));
        });
    }

    function assemblyMessage(cmd, data) {
        var custom_content = {
            "seq": seq,
            "cmd": cmd,
            "data": data
        };
        var custom_msg = {
            "request_id": idName + '-' + custom_seq++,
            "room_id": roomID,
            "from_userid": idName,
            "from_username": nickName,
            "custom_content": JSON.stringify(custom_content)
        };
        return JSON.stringify(custom_msg);
    }

    var operateStatus = false;
    var wwjPlayer = null;
    var waitQueue = [];
    var waitPosition = 0;
    var RECVCMD = {
        broadcast: 257,
        appointment: 272,
        cancelAppointment: 274,
        upSelect: 258,
        upSelectRsp: 273,
        operateResult: 260
    };

    zg.onRecvCustomCommand = function (from_userid, from_username, custom_content) {
        console.log('客户端-onRecvCustomCommand = ', from_userid, from_username, JSON.parse(custom_content));
        custom_content = JSON.parse(custom_content);

        if (custom_content.cmd === RECVCMD.broadcast) {

            broadcastHandler(custom_content);
        } else if (custom_content.cmd === RECVCMD.appointment) {

            appointmentHandler(custom_content);
        } else if (custom_content.cmd === RECVCMD.cancelAppointment) {

            cancelAppointmentHandler();
        } else if (custom_content.cmd === RECVCMD.upSelect) {

            upSelectHandler(custom_content);
        } else if (custom_content.cmd === RECVCMD.upSelectRsp) {
            console.log('operateStatus', operateStatus);

            upSelectRspHandler();
        } else if (custom_content.cmd === RECVCMD.operateResult) {

            operateResultHandler(custom_content);
        }
    };

    zg.onDisconnect = function (err) {
        console.log('客户端-onDisconnect = ', err);
        alert('您断开连接了,请刷新页面！');
        window.history.back();
    };

    zg.onKickOut = function (err) {
        console.log('客户端-onKickOut = ', err);
        alert('您被踢下线了,请刷新页面！');
        window.history.back();
    };

    zg.onStreamExtraInfoUpdated = function (streamList) {};

    zg.onStreamUpdated = function (type, streamList) {
        if (type == ENUM_STREAM_UPDATE_TYPE.added) {
            console.log("streamupdate add");
            useStreamList = updateStreamInfo(streamList);

            if (useStreamList) {
                for (var i = useStreamList.length - 1; i >= 0; i--) {
                    var useFlag = false;
                    var streamInfo = useStreamList[i];
                    for (var j = 0; j < useLocalStreamList.length; j++) {
                        if (useLocalStreamList[j].stream_id === streamInfo.stream_id) {
                            useFlag = true;
                            break;
                        }
                    }

                    if (!useFlag) {
                        useStreamList.push(streamInfo);
                    }
                }
            }
        } else if (type == ENUM_STREAM_UPDATE_TYPE.deleted) {
            console.log("streamupdate delete");
            if (useStreamList) {
                deleteStreamInfo(streamList);
            }
        }
    };

    zg.onPlayStateUpdate = function (type, streamID) {};

    zg.onPlayQualityUpdate = function (streamID, streamQuality) {};

    function movetoleft() {
        $audio.play();
        console.log('向左!');
        sendCustomCMD(528, operateData);
    }

    function movetoright() {
        $audio.play();
        console.log('向右!');
        sendCustomCMD(529, operateData);
    }

    function movetoup() {
        $audio.play();
        console.log('向前!');
        sendCustomCMD(531, operateData);
    }

    function movetodown() {
        $audio.play();
        console.log('向后!');
        sendCustomCMD(530, operateData);
    }

    function appointment() {
        $audio.play();
        console.log('申请预约!');
        sendCustomCMD(513, operateData);
    }

    function cancelAppointment() {
        $audio.play();
        console.log('取消预约!');
        sendCustomCMD(514, operateData);
    }

    var tapParams = {
        timer: {},
        element: {},
        tapStartTime: 0,
        type: 'increment'
    };

    window.clearTapTimer = function () {
        clearTimeout(tapParams.timer);
    };

    window.clearTapHandlers = function () {
        clearTapTimer();

        $(tapParams.element).unbind('mouseup', clearTapTimer).unbind('mouseleave', clearTapHandlers);

        $(tapParams.element).unbind('touchend', clearTapTimer).unbind('touchcencel', clearTapHandlers);
    };

    window.tapEvent = function (aEvent, aType) {
        aEvent.preventDefault();
        aEvent.stopPropagation();

        tapParams = {
            element: aEvent.target,
            startTime: new Date().getTime() / 1000,
            type: aType,
            finish: false
        };

        $(tapParams.element).bind('mouseup', clearTapTimer).bind('mouseleave', clearTapHandlers);

        $(tapParams.element).bind('touchend', clearTapTimer).bind('touchcencel', clearTapHandlers);

        changeMove();
    };

    window.changeMove = function () {
        if (tapParams.finish) {
            clearTapTimer();
            return;
        }

        console.log("move info:", tapParams);
        if (tapParams.type == "front-left" || tapParams.type == "side-up") {
            movetoleft();
        } else if (tapParams.type == "side-left" || tapParams.type == "front-down") {
            movetodown();
        } else if (tapParams.type == "front-right" || tapParams.type == "side-down") {
            movetoright();
        } else if (tapParams.type == "side-right" || tapParams.type == "front-up") {
            movetoup();
        } else {
            console.log("");
        }

        tapParams.timer = setTimeout('changeMove()', 200);
    };

    function gotocatch() {
        $audio.play();
        console.log('go!');

        var catchData = { "time_stamp": operateData.time_stamp, "pay_token": payToken };
        sendCustomCMD(532, catchData);
        clearInterval(countDownTimer);
        clearTapTimer();
        $directWrapper.className += ' disabled';
        tapParams.finish = true;
    }

    function broadcastHandler(custom_content) {
        $audience.innerHTML = custom_content.data.total;
        wwjPlayer = custom_content.data.player;

        if (JSON.stringify(wwjPlayer) === '{}' || wwjPlayer.name === '') {
            hideElement([$anchorWrapper]);
            $anchor.innerHTML = '';
        } else {
            showElement([$anchorWrapper]);
            $anchor.innerHTML = wwjPlayer.name;
        }
        waitQueue = custom_content.data.queue;

        if (waitQueue.length !== 0) {
            $beforeQueueNum.innerHTML = waitQueue.length;
            for (var i = 0; i < waitQueue.length; i++) {
                if (waitQueue[i].id === idName) {
                    waitPosition = i + 1;
                    break;
                }
            }
            $afterQueueNum.innerHTML = waitPosition;
        } else {
            $beforeQueueNum.innerHTML = 0;
        }
    }

    function appointmentHandler(custom_content) {
        if (custom_content.data.result == 1) {
            alert('预约失败！');
            showElement([$apply]);
            hideElement([$cancel]);
        } else {
            $applyWrapper.className += ' disabled';

            waitPosition = custom_content.data.index;
            $afterQueueNum.innerHTML = waitPosition;
            hideElement([$apply, $beforeQueue]);
            showElement([$cancel, $afterQueue]);

            $afterQueueNum.innerHTML = custom_content.data.index;
        }
    }

    function cancelAppointmentHandler() {
        showElement([$apply, $beforeQueue]);
        hideElement([$cancel, $afterQueue]);
    }

    function upSelectHandler(custom_content) {
        var seq258 = custom_content.seq;
        var replyData = { "time_stamp'": 12345, "seq": seq258 };
        sendCustomCMD(516, replyData);
        showElement([$upornotWrapper]);
        countDown($upornotCountDown, upornotCountDownTime, function () {
            hideElement([$upornotWrapper]);
            confirmTocancel();
        });
    }
    $upornotConfirm.addEventListener('click', pay);
    $upornotCancel.addEventListener('click', confirmTocancel);
    function upToPlay() {
        console.log('shangji');
        operateStatus = true;
        var replyData = { "confirm": 1, "time_stamp": 12345, "pay_token": payToken };
        sendCustomCMD(515, replyData);
        hideElement([$upornotWrapper]);
    }
    function confirmTocancel() {
        console.log('quxiao');
        operateStatus = false;
        var replyData = { "confirm": 0, "time_stamp": 12345 };
        sendCustomCMD(515, replyData);
        hideElement([$upornotWrapper, $cancel]);
        showElement([$apply]);
    }

    function RecoveGameStateHandler() {
        if (playingStatus) {
            showElement([$operateWrapper]);
            hideElement([$appointmentWrapper]);

            registerOperateHandler(directMapObj, directHandler);
        }
    }

    function upSelectRspHandler() {
        if (operateStatus) {
            showElement([$operateWrapper, $countDownWrapper]);
            hideElement([$appointmentWrapper]);
            countDown($countDown, playCountDownTime, function () {
                gotocatch();
            });

            registerOperateHandler(directMapObj, directHandler);
        } else {
            hideElement([$cancel, $afterQueue]);
            showElement([$apply, $beforeQueue]);
        }
        $applyWrapper.className = 'apply-wrapper';
    }

    function operateResultHandler(custom_content) {
        var seq258 = custom_content.seq;
        var replyData;
        if (JSON.stringify(custom_content.data.player) === '{}') {
            $apply.innerHTML = '预约抓娃娃';
            hideElement([$operateWrapper, $afterQueue, $countDownWrapper]);
            showElement([$appointmentWrapper, $apply, $beforeQueue]);
        }
        if (custom_content.data.player.id !== idName) return;

        removeAllEventHandler(directMapObj, directHandler);

        replyData = { "time_stamp": 12345, "seq": seq258 };
        sendCustomCMD(517, replyData);

        if (custom_content.data.result === 1) {
            $resultWrapper.className = $resultWrapper.className.replace(/fail/g, '') + ' success';
        } else {
            $resultWrapper.className = $resultWrapper.className.replace(/success/g, '') + ' fail';
        }
        countDown($backCountDown, 10, function () {
            $resultWrapper.className = $resultWrapper.className.replace(/(success|fail)/g, '');
        });
        hideElement([$operateWrapper, $cancel, $afterQueue, $countDownWrapper]);
        showElement([$appointmentWrapper, $apply, $beforeQueue]);

        $directWrapper.className = $directWrapper.className.replace(/disabled/g, '');
        $apply.innerHTML = '预约抓娃娃';
    }

    errNum++;

    window.addEventListener("error", function () {
        alert('发生错误码 = ' + errNum + ', 请把该错误截图，联系即构客服！');
    }, !0);
};
/* WEBPACK VAR INJECTION */}.call(exports, __webpack_require__(12)))

/***/ })
],[15]);