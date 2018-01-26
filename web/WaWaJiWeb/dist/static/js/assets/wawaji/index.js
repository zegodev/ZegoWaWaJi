webpackJsonp([0],[
/* 0 */
/***/ (function(module, exports, __webpack_require__) {

/*eslint-disable no-console */

// 调试移动端用，相当于浏览器的控制台
// 生产环境下可以去掉
// const VConsole = require('../../static/js/vconsole-3.0.0/vconsole.min.js');
// new VConsole();

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

// demo采用require模式加载，也可以采用script加载模式  示例在wawaji页面上，script标签内，已经注释
// 关于平台判断客户可以根据自己的实际情况自己设置
// demo仅供参考
var ZegoClient = void 0;
console.log('navigator = ', navigator);
var ua = navigator.userAgent.toLowerCase();
if (/iphone|ipad|ipod/i.test(ua)) {
    // 在ios中
    if (ua.match(/MicroMessenger/i) == "micromessenger") {
        // 微信中
        if (window.__wxjs_is_wkwebview === true) {
            // wkwebview
            setSdk(true);
            console.log('ios 微信 wk');
        } else {
            // UIWebView
            setSdk(false);
            console.log('ios 微信 ui');
        }
    } else if (/QQ|UIWebView/i.test(ua)) {
        // 在QQ中
        console.log('ios QQ ui');
        setSdk(false);
    } else {
        setSdk(true);
        console.log('ios 其他');
    }
} else if (/Android/i.test(ua)) {
    // 在android中
    var index = ua.indexOf("android");
    var androidVersion = parseFloat(navigator.userAgent.slice(index + 8));
    console.log(androidVersion);
    if (androidVersion < 4.4) {
        setSdk(false);
        console.log('android 版本小于4.4');
    } else {
        setSdk(true);
        console.log('android 版本大于4.4');
    }
} else {
    // 电脑或者其他平台
    setSdk(true);
}
function setSdk(type) {
    if (type) {
        // 同步版本  适用于性能好的机器
        window.quality = true;
        ZegoClient = __webpack_require__(22);
    } else {
        // 异步版本  适用于性能不好的机器  视频将会码率降级
        window.quality = false;
        ZegoClient = __webpack_require__(21);
    }
}
// 工具函数
var util = __webpack_require__(17);

var xmlhttp2, workerPlayerJSBlob;
xmlhttp2 = new XMLHttpRequest();
xmlhttp2.onreadystatechange = function () {
    if (xmlhttp2.readyState == 4) {
        if (xmlhttp2.status == 200) {
            workerPlayerJSBlob = new Blob([xmlhttp2.responseText], { type: 'text/javascript' });
        } else {
            alert("获取stub文件失败");
        }
    }
};
xmlhttp2.open("GET", '/static/js/jZego/jsmpeg-stub.min.js', false);
xmlhttp2.send();
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

// var appid = 4095207472;                                                         //appid
var appid = 3265350344; //appid

var roomID = ""; //房间id
var idName = ""; //用户id
var nickName = ""; //用户昵称
var anchor_id = ""; //娃娃机主播id


// 链接websocket
var server = '';
var logUrl = '';
var loginTokenUrl = '';
var payTokenUrl = '';
if (window.location.protocol === 'https:') {
    server = 'wss://wsliveroom' + appid + '-api.zego.im:8282/ws'; //wawaji接入服务器地址    --- 即构下发的server地址
    logUrl = ''; //log服务器地址          --- 可填可不填
    loginTokenUrl = 'https://wsliveroom' + appid + '-api.zego.im:8282/token'; //登录token派发地址       --- 业务后台自己的地址
    payTokenUrl = 'https://wsliveroom' + appid + '-api.zego.im:8282/pay'; //支付地址               --- 业务后台自己的地址
} else {
    server = 'ws://wsliveroom' + appid + '-api.zego.im:8181/ws'; //wawaji接入服务器地址    --- 即构下发的server地址
    logUrl = ''; //log服务器地址          --- 可填可不填
    loginTokenUrl = 'http://wsliveroom' + appid + '-api.zego.im:8181/token'; //登录token派发地址       --- 业务后台自己的地址
    payTokenUrl = 'http://wsliveroom' + appid + '-api.zego.im:8181/pay'; //支付地址               --- 业务后台自己的地址
}

var zg; //zegoClient对象
var clientSeq = 1; //发送客户端请求seq
var serverSeq = 0; //发送服务端返回seq

var playCountDownTime = 30; //游戏总时长
var upornotCountDownTime = 10; //上机确认超时时长
var countDownTimer; //超时timer

var isInitApply = true; // 是否是从预约按钮开始预约，还是从结果页开始自动预约

var loginToken = ""; //登录令牌
var payToken = ""; //支付令牌
var itemType = "itme_type1"; //商品类型
var itemPrice = 6; //商品价格

var payTimestamp = 0;

var hasCatch = localStorage.getItem('hasCatch'); // 判断用户是否下抓
if (hasCatch === null) {
    hasCatch = 'yes';
}

var gameStatus = localStorage.getItem('gameStatus'); // 判断当前游戏是否结束
if (gameStatus === null) {
    gameStatus = 'gameover';
}
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
// roomID = 'WWJ_ZEGO_00d200ce17bf';
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
        remoteLogLevel: 0,
        // workerUrl: 'http://www.zego.im/static/js/jZego/jsmpeg-stub.min.js'   // 自定义jsmpeg.stub.min.js的引入地址
        workerPlayerJSBlob: workerPlayerJSBlob // jsmpeg.stub.min.js内容Blob化的数据格式

        // 1、针对低性能手机/webview需要使用async异步sdk版本，配置辅助文件jsmpeg.stub.min.js，作为worker运行在浏览器后台对视频进行解码操作，
        // 性能好的手机/webview依旧使用同步sdk版本

        // 2、同步与异步版本的sdk接口没有变化，只有在startPlayingStream这个接口增加了第四个参数，参数值为字符串的1,
        // 例子： zg.startPlayingStream(streamID, view, viewMode, '1'); 表示调用降级后的视频流，视频流比原先降了4分之一质量；

        // 3、异步sdk的辅助文件jsmpeg.stub.min.js的引入方式有三种，开发者可自己决定：
        // （1）jsmpeg.stub.min.js和异步sdk文件放置于同一个目录下，异步sdk内部会自动去同级目录下加载jsmpeg.stub.min.js
        // （2）调用zg.config接口，配置workerUrl参数，该参数表示jsmpeg.stub.min.js的引入路径
        //      例子 zg.config({ workerUrl: 'http://www.zego.im/static/js/jZego/jsmpeg-stub.min.js' });
        // 浏览器不允许跨域请求资源，此处url需要是同域名下的地址
        // （3）调用zg.config接口，配置workerPlayerJSBlob参数，该参数表示jsmpeg.stub.min.js内容Blob化后的数据格式（可解决跨域问题）
        // 例子： 
        // var xmlhttp2,workerPlayerJSBlob;
        // xmlhttp2 = new XMLHttpRequest();
        // xmlhttp2.onreadystatechange = function() {
        //     if (xmlhttp2.readyState == 4) {
        //         if (xmlhttp2.status == 200) {
        //             workerPlayerJSBlob = new Blob([xmlhttp2.responseText], {type : 'text/javascript'});
        //         }
        //     }
        // };
        // xmlhttp2.open("GET", '/static/js/jZego/jsmpeg-stub.min.js', false);
        // xmlhttp2.send();
        // zg.config({ workerPlayerJSBlob: workerPlayerJSBlob });
        // （4）workerUrl与workerPlayerJSBlob二者选其一，如果都配置以workerPlayerJSBlob为优先
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
        sendCustomCMD(sendClientSeq(), 518, operateData);
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
                for (var k = 0; k < useLocalStreamList.length; k++) {
                    reUseFlag = false;
                    for (var j = 0; j < useStreamList.length; j++) {
                        // 判断登录成功后的流信息中的stream_id和本地的是否相等，相等的话则该流没有变化，存起来
                        if (useLocalStreamList[k].stream_id === useStreamList[j].stream_id) {
                            reUseStreamList.push(useLocalStreamList[k].stream_id);
                            reUseFlag = true;
                            break;
                        }
                    }
                    // 服务端推过来的add类型的流，不做停止播流处理  -----   断线重连后，可能推来的流信息会发生变化，变化的本地流销毁 / 停止播放
                    if (updateType != ENUM_STREAM_UPDATE_TYPE.added && !reUseFlag) {
                        zg.stopPlayingStream(useLocalStreamList[k].stream_id);
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
                    if (window.quality) {
                        console.log('window.quality1 = ', window.quality);
                        zg.startPlayingStream(useStreamList[m].stream_id, useStreamList[m].videoView);
                    } else {
                        console.log('window.quality2 = ', window.quality);
                        zg.startPlayingStream(useStreamList[m].stream_id, useStreamList[m].videoView, '', '1');
                    }
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
        // 测试发送房间消息
        // 主要前两个参数事纯数字类型，否则发送不成功
        // zg.sendRoomMsg(2, 1, 'sdfsdf', function(seq, msdid, msgcategory, msgtype, msgcontent){
        //     console.log(seq, msdid, msgcategory, msgtype, msgcontent);
        // },function(err, seq, msgcategory, msgtype, msgcontent){
        //     console.log(err, seq, msgcategory, msgtype, msgcontent);
        // });
        playAudio();
        viewStatus = e.target.dataset['switch'];
        if (viewStatus === '1') {
            // 此时处于正面，切换为侧面
            e.target.dataset['switch'] = '0';
            util.removeClass($viewWrapper, 'front');
            util.removeClass($directWrapper, 'front');
            //updateStreamDecode(false);
        } else {
            // 此时处于侧面，切换为正面
            e.target.dataset['switch'] = '1';
            util.addClass($viewWrapper, 'front');
            util.addClass($directWrapper, 'front');
            //updateStreamDecode(true);
        }
    });

    /*
    function updateStreamDecode(frontDecode) {
         for (var i = 0; i < useLocalStreamList.length; i++) {
            if (useLocalStreamList[i].stream_id.endsWith("_2")) {
                zg.enableDecode(useLocalStreamList[i].stream_id, !frontDecode);
            }
            else {
                zg.enableDecode(useLocalStreamList[i].stream_id, frontDecode);
            }
        }
    }
    */

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
        sendCustomCMD(sendClientSeq(), 513, configData);
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
        sendCustomCMD(sendClientSeq(), 514, operateData);
    }

    // 左移动
    function movetoleft(seq) {
        var curSeq = seq || sendClientSeq();
        playAudio();
        console.log('向左!');
        sendCustomCMD(curSeq, 528, operateData);
    }
    // 右移动
    function movetoright(seq) {
        var curSeq = seq || sendClientSeq();
        playAudio();
        console.log('向右!');
        sendCustomCMD(curSeq, 529, operateData);
    }
    // 前移动
    function movetoup(seq) {
        var curSeq = seq || sendClientSeq();
        playAudio();
        console.log('向前!');
        sendCustomCMD(curSeq, 531, operateData);
    }
    // 后移动
    function movetodown(seq) {
        var curSeq = seq || sendClientSeq();
        playAudio();
        console.log('向后!');
        sendCustomCMD(curSeq, 530, operateData);
    }
    // 停止移动
    function stopmove() {
        console.log('停止移动!');
        sendCustomCMD(sendClientSeq(), 533, operateData);
    }
    // 抓取娃娃
    function gotocatch() {
        hasCatch = 'yes';
        localStorage.setItem('hasCatch', hasCatch);

        playAudio();
        console.log('go!');

        var catchData = { "time_stamp": operateData.time_stamp };
        sendCustomCMD(sendClientSeq(), 532, catchData);
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
            "session_id": localStorage.getItem('sessionID'),
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
        sendClientSeq();
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

        // 发送取消预约指令
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

                // waitPosition = custom_content.data.index;
                // $afterQueueNum.innerHTML = waitPosition;
                util.hideElement([$apply, $beforeQueue]);
                util.showElement([$cancel, $afterQueue]);
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
        // 过滤258指令
        // 因为娃娃机如果收到516回包失败，会连续发送5次258指令，该指令seq一样，我们需要过滤
        if (serverSeq === custom_content.seq) {
            return;
        } else {
            serverSeq = custom_content.seq;
        }
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
        sendCustomCMD(sendClientSeq(), 515, replyData);
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
        sendCustomCMD(sendClientSeq(), 515, replyData);
    }

    // 第五消息类
    // 处理收到的 服务端返回的信息   对客户端发送的确认上机或者放弃玩游戏指令的回应   273  对应   515 上机或放弃
    function upSelectRspHandler(custom_content) {
        var resultCode = custom_content.data.result;
        if (resultCode === 1) {
            console.log('发送确认上机的信息---格式无效！');
            clearInterval(upToPlayTimer);
            return;
        } else if (resultCode === 2) {
            console.log('发送确认上机的信息---校验失败！');
            clearInterval(upToPlayTimer);
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

            // 处于游戏状态
            gameStatus = 'playing';
            localStorage.setItem('gameStatus', gameStatus);
            // 用户下抓状态重置为  未下抓
            hasCatch = 'no';
            localStorage.setItem('hasCatch', hasCatch);
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
    // 处理收到的 收到本次抓娃娃的结果    260
    function operateResultHandler(custom_content) {
        gameStatus = 'gameover'; // 设置游戏处于结束状态
        localStorage.setItem('gameStatus', gameStatus);
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
        $playAgainCountDown.innerHTML = upornotCountDownTime;
        util.hideElement([$operateWrapper, $cancel, $afterQueue]);
        util.showElement([$appointmentWrapper, $apply, $beforeQueue]);

        util.removeClass($directWrapper, 'disabled');
        $apply.innerHTML = '预约抓娃娃';

        // 不管成功或者失败，都移除方向操作和抓取动作的事件
        util.removeAllEventHandler(directMapObj, directHandler);

        // 回复结果
        console.log('发送继续玩指令！');
        var replyData = { "time_stamp": new Date().getTime(), continue: 1 };
        sendCustomCMD(sendClientSeq(), 517, replyData);
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
        sendCustomCMD(sendClientSeq(), 513, configData);
        isInitApply = false;
    }

    // 第八消息类
    // 获取到当前房间游戏信息，并设置房间人数，和当前排队人数 275
    function gameInfoHandler(custom_content) {
        // 设置当前排队人数，和房间总人数
        var gameInfo = custom_content.data;
        console.log('获取游戏信息= ', custom_content);
        $beforeQueueNum.innerHTML = gameInfo.queue.length;
        $audience.innerHTML = gameInfo.total;
        var leftTime = gameInfo.player.left_time;

        // 如果当前在游戏的主播id和本地的idName一样，则恢复游戏状态  -----  如正在游戏，刷新页面场景
        if (gameInfo.player.id === idName) {
            gameStatus = localStorage.getItem('gameStatus');
            console.log('gameStatus = ', gameStatus);
            if (gameStatus == 'playing') {
                recoveGameStateHandler(leftTime);
            } else if (gameStatus == 'gameover') {
                // 场景：游戏结束，自动预约，此时刷新页面，应该回到初始界面，并且取消预约之前的自动预约，取消上机之前可能的上机
                // 发送取消预约指令
                cancelAppointmentClientHandler();
            }
        }
        // 如果是预约了，然后刷新页面，在队列里面发现还在排队，则主动发送取消预约指令
        if (gameInfo.queue.length !== 0) {
            for (var i = 0; i < gameInfo.queue.length; i++) {
                if (gameInfo.queue[i].id == idName) {
                    // 发送取消预约指令
                    cancelAppointmentClientHandler();
                }
            }
        }
    }
    // 恢复游戏中状态处理
    function recoveGameStateHandler(leftTime) {
        util.showElement([$operateWrapper]);
        util.hideElement([$appointmentWrapper]);
        if (localStorage.getItem('hasCatch') === 'yes') {
            // 如果已经下抓，就隐藏游戏时间倒计时
            util.hideElement([$countDownWrapper]);
        } else {
            countDown($countDown, leftTime, function () {
                gotocatch();
            });
        }
        sessionID = util.getLocal('sessionID');
        util.registerOperateHandler(directMapObj, directHandler);
    }

    // 接收房间IM消息
    zg.onRecvRoomMsg = function (chat_data, server_msg_id, ret_msg_id) {
        console.log('接收房间IM消息 = ', chat_data, server_msg_id, ret_msg_id);
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
        console.log('客户端-onStreamUpdated = ', type, streamList);
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
    // 房间成员变化回调
    // zg.onUserStateUpdate = function(roomId, userList){
    //     console.log('房间成员变化回调 = ', roomId, userList);
    // };

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
                        // 隐藏loading图/poster图案
                        useLocalStreamList[i].videoView.nextElementSibling.style.display = 'none';
                    }
                }
            }
        }
    };

    // 服务端主动推过来的 流的质量更新
    zg.onPlayQualityUpdate = function (streamID, streamQuality) {
        // code 业务逻辑
        console.log('客户端-onPlayQualityUpdate = ', streamID, streamQuality.fps, streamQuality.kbs);
    };

    // 服务端主动推过来的 房间内用户人数变化通知
    zg.onUserStateUpdate = function (roomID, userList) {
        console.log('客户端-onUserStateUpdate = ', roomID, userList);
    };

    // 回调当前在线用户列表 （登录房间成功后自动拉取)
    zg.onGetTotalUserList = function (roomID, userList) {
        console.log('客户端-onGetTotalUserList = ', roomID, userList);
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
                return false;
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
                    if (ext) {
                        data.msg = ext.toString();
                    }
                }
                //把data上报到后台！
                console.log('data = ', data);
                alert('发生错误 = ' + JSON.stringify(data));
            }, 0);
        }

        return false;
    };
};

// 工具函数
var localClientSeq;
// 返回clientSeq
function sendClientSeq() {
    localClientSeq = localStorage.getItem('clientSeq');
    if (localClientSeq !== null) {
        clientSeq = parseInt(localClientSeq);
    }
    ++clientSeq;
    localStorage.setItem('clientSeq', clientSeq);
    return clientSeq;
}

// 上机倒计时，游戏倒计时，再玩一次倒计时
function countDown(dom, countNum, cb) {
    console.log('dom = ', dom);
    if (countDownTimer) {
        clearInterval(countDownTimer);
    }
    var innerCountNum = countNum;
    dom.innerHTML = innerCountNum;
    countDownTimer = setInterval(function () {
        // console.log('dom = ', dom);
        // console.log('innerCountNum = ', innerCountNum);
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

/***/ }),
/* 19 */,
/* 20 */,
/* 21 */
/***/ (function(module, exports, __webpack_require__) {

!function(t,e){ true?module.exports=e():"function"==typeof define&&define.amd?define(e):t.ZegoClient=e()}(this,function(){"use strict";function t(t,e){this._id="number"==typeof t?t:null,this._data=e||null,this.next=null,this.prev=null}function e(){this.start=new t,this.end=new t,this.start.next=this.end,this.start.prev=null,this.end.prev=this.start,this.end.next=null,this._idCounter=0,this._numNodes=0}function i(){this.logSeq=0,this.logLevel=P.disable,this.logRemoteLevel=P.disable,this.websocket=null,this.url="",this.appid=0,this.sessionid="0",this.roomid="",this.userid="",this.userName="",this.logCache=[],this.logCacheSend=[],this.logCacheMax=100}function r(t,e,i){var r=new Date,s=1900+r.getYear()+"/";s+=(D[r.getMonth()+1]||r.getMonth()+1)+"/",s+=(D[r.getDate()]||r.getDate())+" ",s+=(D[r.getHours()]||r.getHours())+":",s+=(D[r.getMinutes()]||r.getMinutes())+":",s+=D[r.getSeconds()]||r.getSeconds(),s+="."+r.getTime()%1e3;var o=i.substr(0,i.indexOf(" "));0==o.length&&(o=i);var n=i.substr(i.indexOf(" ")+1);0==n.length&&(n="");var a={time:s,level:e,action:o,content:n,appid:t.appid,roomid:t.roomid,userid:t.userid,userName:t.userName,sessionid:t.sessionid};return[JSON.stringify(a)]}function s(t,e,i,r,s,o,n,a){this.streamid=e,this.viewMode=o,this.urls=i,this.playUrlIndex=0,this.playUrlTryCount=0,this.view=r,this.reconnectLimit=s,this.reconnectCount=0,this.state=B.stop,this.playerStat={videoBytes:0,videoFrameCnt:0,videoDecodeFrameCnt:0},this.player=null,this.stateTimeStamp=0,this.logger=t,this.firstCallback=!0,this.decode=!0,this.workerUrl=n,this.workerPlayerJSBlob=a}function o(t){this.sourceType=M.cdn,this.playerList={},this.playerCount=0,this.playerMonitorTimer=null,this.playerMonitorInterval=1e3,this.playerStartTimeout=5e3,this.playerStartBitrate=12500,this.playerStopBitrate=1250,this.playerStopTimeout=5e3,this.logger=t,this.playerInfo={}}function n(t,e){for(;e.playUrlTryCount<e.urls.length;)if(++e.reconnectCount>e.reconnectLimit)e.playUrlTryCount++,e.playUrlIndex=(e.playUrlIndex+1)%e.urls.length,e.reconnectCount=0;else if(t.logger.info("tsp.0 streamid: "+e.streamid+", index:"+e.playUrlIndex+", url:"+e.getCurrentPlayerUrl()),e.newPlayer(t,t.sourceType))break;return e.playUrlTryCount>=e.urls.length&&(t.logger.info("tsp.1 stream: ",e.streamid),e.resetPlayer(),F.DestroySingleRenderer(e.view.id),delete t.playerList[e.streamid],--t.playerCount,t.onPlayStateUpdate(N.error,e.streamid)),a(t),!0}function a(t){t.playerCount>0?t.playerMonitorTimer||(t.logger.debug("upm.1 called"),t.playerMonitorTimer=setInterval(function(){!function(t){var e,i,r,s,o=Date.now(),l=0;for(var d in t.playerList)if(e=t.playerList[d]){if(i=e.player.playoutStatus.videoBytes-e.playerStat.videoBytes,r=e.player.playoutStatus.videoFrameCnt-e.playerStat.videoFrameCnt,s=e.player.playoutStatus.videoDecodeFrameCnt-e.playerStat.videoDecodeFrameCnt,l=o-e.stateTimeStamp,e.state===U.start)if(i>=l/1e3*t.playerStartBitrate&&s>0){e.state=U.playing,e.stateTimeStamp=o,e.playUrlTryCount=0,e.updatePlayerStat(),t.logger.info("ups.1 streamid: ",e.streamid),e.firstCallback&&(e.firstCallback=!1,t.onPlayStateUpdate(N.start,e.streamid));var u=t.playerInfo[d];void 0!==u?e.enableDecode(u):(t.logger.info("ups.1 don't set decode: "+e.streamid),u=!0),u&&h(t,e,i,r,s,l)}else l>=t.playerStartTimeout&&(t.logger.info("ups.2 streamid: "+e.streamid),n(t,e));else e.state===U.playing&&l>=t.playerStopTimeout&&e.decode&&(i<l/1e3*t.playerStopBitrate?(e.state=U.stop,e.stateTimeStamp=o,t.logger.info("ups.3 streamid: "+e.streamid),n(t,e)):(e.stateTimeStamp=o,e.updatePlayerStat(),e.decode&&h(t,e,i,r,s,l)));t.logger.debug("ups.0 streamid: "+d+": videoBytesAdd="+i+"videoFrameCntAdd="+r+"videoDecodeFrameCntAdd="+s)}else delete t.playerList[d];a(t)}(t)},t.playerMonitorInterval)):t.playerMonitorTimer&&(t.logger.debug("upm.2 called"),clearInterval(t.playerMonitorTimer),t.playerMonitorTimer=null)}function h(t,e,i,r,s,o){var n=s/(o/1e3),a=8*i/o,h={fps:n,kbs:a};t.onPlayQualityUpdate(e.streamid,h),t.logger.info("oups.1 streamid:"+e.streamid+"fps="+n+" kbs="+a)}function l(){this.appid=0,this.server="",this.idName="",this.nickName="",this.configOK=!1,this.logger=new i,this.userStateUpdate=!1,this.userSeq=0,this.userQuerying=!1,this.userTempList=[],this.roomid="",this.token="",this.role=0,this.callbackList={},this.runState=q.logout,this.lastRunState=q.logout,this.userid="",this.sessionid="",this.cmdSeq=0,this.websocket=null,this.globalHeader=null,this.tryLoginCount=0,this.tryLoginTimer=null,this.tryHeartbeatCount=0,this.tryHeartbeatTimer=null,this.heartbeatInterval=3e4,this.streamList=[],this.streamQuerying=!1,this.mapStreamDom={},this.mapStreamQuality={},this.preferPlaySourceType=O.cdn,this.wsPlayerList={},this.playerCenter=new o(this.logger),this.playerCenter.onPlayStateUpdate=this.onPlayStateUpdateHandle.bind(this),this.playerCenter.onPlayQualityUpdate=this.onPlayQualityUpdateHandle.bind(this),this.playerStateTimer=null,this.playerStateInterval=2e3,this.sendDataMap={},this.sendDataList=new e,this.sendDataCheckTimer=null,this.sendDataCheckInterval=2e3,this.sendDataTimeout=5e3,this.sendDataDropTimeout=1e4,this.sendDataCheckOnceCount=100,this.sendRoomMsgTime=0,this.SendRoomMsgInterval=500}function d(t,e,i){t.logger.debug("zc.p.dps.0 call");for(var r=null,s=0,o=0;o<t.streamList.length;o++)if(t.streamList[o].stream_id===e){r=t.streamList[o].urls_ws||[],s=t.streamList[o].viewMode;break}return!(!r||r.length<=0)&&(t.logger.info("zc.p.dps.0 streamid: "+e+" streamUrl: "+r),t.playerCenter.startPlayingStream(e,r,i,s,t.workerUrl,t.workerPlayerJSBlob),t.logger.debug("zc.p.dps.0 call success"),!0)}function u(t,e){return t.callbackList[e+"ErrorCallback"]}function c(t){var e={1:"parse json error.",1001:"login is processing.",1002:"liveroom request error.",1003:"zpush connect fail.",1004:"zpush handshake fail.",1005:"zpush login fail.",1006:"user login state is wrong.",1007:"got no zpush addr",1008:"token error",1009:"dispatch error",1e9:"liveroom cmd error, result="};if(0===t)return{code:"ZegoClient.Success",msg:"success"};var i={};return i.code="ZegoClient.Error.Server",i.msg=t>1e9?e[1e9]+t:void 0!=e[t]?e[t]:"unknown error code:"+t,i}function p(t,e){t.logger.debug("srs.0 old="+t.runState+", new="+e),t.lastRunState=t.runState,t.runState=e}function g(t,e){return t.globalHeader={Protocol:"req",cmd:e,appid:t.appid,seq:++t.cmdSeq,user_id:t.userid,session_id:t.sessionid,room_id:t.roomid},t.globalHeader}function f(t,e,i){if(t.logger.debug("sm.0 call "+e),t.websocket&&1===t.websocket.readyState){var r={header:g(t,e),body:i},s=JSON.stringify(r);t.websocket.send(s),t.logger.debug("sm.0 success")}else t.logger.info("sm.0 error")}function m(t,e,i,r,s){if(t.logger.debug("scm.0 call"),!t.websocket||1!==t.websocket.readyState)return t.logger.info("scm.0 error"),!1;var o=g(t,e),n={header:o,body:i},a=JSON.stringify(n);void 0==r&&(r=null),void 0==s&&(s=null);var h={data:n,seq:o.seq,deleted:!1,time:Date.parse(new Date),success:r,error:s},l=t.sendDataList.push(h);return t.sendDataMap[h.seq]=l,t.websocket.send(a),t.logger.debug("scm.0 success seq: ",o.seq),!0}function b(t){if(t.logger.debug("scmt.0 call"),t.runState===q.login){for(var e=t.sendDataList.getFirst(),i=Date.parse(new Date),r=0,s=0,o=0;!(null==e||e._data.time+t.sendDataTimeout>i||(delete t.sendDataMap[e._data.data.header.seq],t.sendDataList.remove(e),++s,null==e._data.error||t.sendDataDropTimeout>0&&e._data.time+t.sendDataDropTimeout>i?++o:e._data.error(Q.SEND_MSG_TIMEOUT,e._data.data.header.seq,e._data.data.body.custom_msg),++r>=t.sendDataCheckOnceCount));)e=t.sendDataList.getFirst();t.sendDataCheckTimer=setTimeout(function(){b(t)},t.sendDataCheckInterval),t.logger.debug("scmt.0 call success, stat: timeout=",s,"drop=",o)}else t.logger.info("scmt.0 state error")}function y(t){t.logger.debug("rcm.0 call"),clearTimeout(t.sendDataCheckTimer),t.sendDataCheckTimer=null;for(var e=t.sendDataList.getFirst();null!=e;)t.sendDataList.remove(e),null!=e._data.error&&e._data.error(Q.RESET_QUEUE,e._data.data.header.seq,e._data.data.body.custom_msg),e=t.sendDataList.getFirst();t.sendDataMap={},t.logger.debug("rcm.0 call success")}function v(t){t.logger.debug("rht.0 call"),clearTimeout(t.heartbeatTimer),t.heartbeatTimer=null,t.tryHeartbeatCount=0,t.logger.debug("rht.0 call success")}function T(t){if(t.logger.debug("sht.0 call"),t.runState===q.login){if(++t.tryHeartbeatCount>G)return t.logger.error("sht.0 come to try limit"),p(t,q.logout),E(t),void t.onDisconnect(Q.HEARTBEAT_TIMEOUT);t.logger.debug("sht.0 send packet");f(t,"hb",{reserve:0}),t.heartbeatTimer=setTimeout(function(){T(t)},t.heartbeatInterval),t.logger.debug("sht.0 call success")}else t.logger.info("sht.0 state error")}function w(t){t.logger.debug("rtl.0 call"),clearTimeout(t.tryLoginTimer),t.tryLoginTimer=null,t.tryLoginCount=0,t.logger.debug("rtl.0 call success")}function C(t){if(t.logger.debug("tl.0 call"),t.runState===q.trylogin){if(++t.tryLoginCount>Y){t.logger.error("tl.0 fail times limit");var e=t.lastRunState;return p(t,q.logout),E(t),void(e==q.login?(t.logger.info("tl.0 fail and disconnect"),t.onDisconnect(Q.LOGIN_DISCONNECT)):(t.logger.info("tl.0 fail and callback user"),u(t,"login")(Q.LOGIN_TIMEOUT)))}if(t.websocket&&1===t.websocket.readyState){t.logger.info("tl.0 use current websocket and sent login");var i=_(t);f(t,"login",i)}else{t.logger.debug("tl.0 need new websocket");try{t.websocket&&(t.logger.info("tl.0 close error websocket"),t.websocket.onclose=null,t.websocket.onerror=null,t.websocket.close(),t.websocket=null),t.logger.debug("tl.0 new websocket"),t.websocket=new WebSocket(t.server),t.websocket.onopen=function(){t.logger.info("tl.0 websocket.onpen call"),function(t){t.websocket.onmessage=function(e){var i=JSON.parse(e.data);if(t.logger.debug("jsonmsg=",JSON.parse(e.data)),"login"!==i.header.cmd)if(i.header.appid===t.appid&&i.header.session_id===t.sessionid&&i.header.user_id===t.userid&&i.header.room_id===t.roomid&&t.runState===q.login)switch(i.header.cmd){case"hb":!function(t,e){if(t.logger.debug("hhbr.0 call stream_seq "+e.body.stream_seq+" user_seq "+e.body.server_user_seq),0!==e.body.err_code){t.logger.info("hhbr.0 call disconnect, server error=",e.body.err_code),p(t,q.logout),E(t);var i=c(e.body.err_code);return void t.onDisconnect(i)}t.tryHeartbeatCount=0,t.heartbeatInterval=e.body.hearbeat_interval,t.heartbeatInterval<W&&(t.heartbeatInterval=W);e.body.stream_seq!==t.streamSeq&&(t.logger.info("hhbr.0 call update stream"),x(t));e.body.server_user_seq!==t.userSeq&&t.userStateUpdate&&(t.logger.info("hhbr.0 call update user "+e.body.server_user_seq,t.userSeq),R(t));t.logger.debug("hhbr.0 call success")}(t,i);break;case"logout":!function(t,e){t.logger.debug("hlor.0 result=",e.body.err_code)}(t,i);break;case"custommsg":!function(t,e){t.logger.debug("hscmr.0 call");var i,r=t.sendDataMap[e.header.seq];null!=r?("custommsg"!=(i=r._data).data.header.cmd?t.logger.error("hscmr.0 cmd wrong"+i.data.header.cmd):0===e.body.err_code?null!=i.success&&i.success(e.header.seq,i.data.body.custom_msg):null!=i.error&&i.error(c(e.body.err_code),e.header.seq,i.data.body.custom_msg),delete t.sendDataMap[e.header.seq],t.sendDataList.remove(r)):t.logger.error("hscmr.0 no found seq="+e.header.seq);t.logger.debug("hscmr.0 call success")}(t,i);break;case"stream_info":!function(t,e){if(t.logger.debug("hfslr.0 call"),t.streamQuerying=!1,0!==e.body.err_code)return void t.logger.info("hfslr.0 server error=",e.body.err_code);if(t.streamSeq===e.body.stream_seq)return void t.logger.info("hfslr.0 same seq");S(t,e.body.stream_seq,e.body.stream_info),t.logger.debug("hfslr.0 call success")}(t,i);break;case"push_custommsg":!function(t,e){var i=JSON.parse(e.body.custommsg);t.logger.debug("hpcm.0 submsg=",i),t.onRecvCustomCommand(i.from_userid,i.from_username,i.custom_content)}(t,i);break;case"push_stream_update":!function(t,e){if(t.logger.debug("hpsum.0 call"),!e.body.stream_info||0===e.body.stream_info.length)return void t.logger.info("hpsum.0, emtpy list");if(e.body.stream_info.length+t.streamSeq!==e.body.stream_seq)return t.logger.info("hpsum.0 call updatestream"),void x(t);switch(t.streamSeq=e.body.stream_seq,e.body.stream_cmd){case H.added:!function(t,e){t.logger.debug("hasl.0 call");for(var i,r=[],s=0;s<e.length;s++){i=!1;for(var o=0;o<t.streamList.length;o++)if(e[s].stream_id===t.streamList[o].stream_id){i=!0;break}i||r.push(e[s])}0!==r.length&&(t.logger.debug("hasl.0 callback addstream"),t.streamList.concat(r),t.onStreamUpdated(V.added,k(r)));t.logger.debug("hasl.0 call success")}(t,e.body.stream_info);break;case H.deleted:!function(t,e){t.logger.debug("hdsl.0 call");for(var i=[],r=0;r<e.length;r++)for(var s=t.streamList.length-1;s>=0;s--)if(e[r].stream_id===t.streamList[s].stream_id){t.streamList.splice(s,1),i.push(e[r]);break}0!==i.length&&(t.logger.debug("hdsl.0 callback delstream"),t.onStreamUpdated(V.deleted,k(i)));t.logger.debug("hdsl.0 call")}(t,e.body.stream_info);break;case H.updated:!function(t,e){t.logger.debug("husl.0 call");for(var i=[],r=0;r<e.length;r++)for(var s=0;s<t.streamList.length;s++)if(e[r].stream_id===t.streamList[s].stream_id){e[r].extra_info!==t.streamList[s].extra_info&&(t.streamList[s]=e[r],i.push(e[r]));break}0!==i.length&&(t.logger.debug("husl.0 callback updatestream"),t.onStreamExtraInfoUpdated(k(i)));t.logger.debug("husl.0 call success")}(t,e.body.stream_info)}t.logger.debug("hpsum.0 call success")}(t,i);break;case"push_kickout":!function(t,e){t.logger.info("hpk.0 call"),p(t,q.logout),E(t);var i={code:Q.KICK_OUT.code,msg:Q.KICK_OUT.msg+e.body.reason};t.onKickOut(i),t.logger.debug("hpk.0 call success")}(t,i);break;case"stream_url":!function(t,e){if(t.logger.debug("hfsur.0 call"),t.streamQuerying=!1,0!==e.body.err_code)return void t.logger.info("hfsur.0 server error=",e.body.err_code);if(e.body.stream_url_infos&&e.body.stream_url_infos.length>0){var i=e.body.stream_url_infos[0].stream_id,r=e.body.stream_url_infos[0].urls_ws;if(t.streamList.length>0)for(var s=0;s<t.streamList.length;s++){var o=t.mapStreamQuality[i];if(t.streamList[s].stream_id===i){t.streamList[s].urls_ws=r,t.streamList[s].quality=o;break}t.streamList.push({stream_id:i,urls_ws:r,quality:o}),o&&(t.logger.debug("hfsur.0 remove quality"),delete t.mapStreamQuality[i])}else t.streamList.push({stream_id:i,urls_ws:r});var n=t.mapStreamDom[i];n&&(t.logger.debug("hfsur.0 play"),delete t.mapStreamDom[i],d(t,i,n))}}(t,i);break;case"im_chat":!function(t,e){t.logger.debug("hsrmr.0 call");var i,r=t.sendDataMap[e.header.seq];null!=r?("im_chat"!=(i=r._data).data.header.cmd?t.logger.error("hsrmr.0 cmd wrong"+i.data.header.cmd):0===e.body.err_code?i.success&&i.success(e.header.seq,e.body.msg_id,i.data.body.msg_category,i.data.body.msg_type,i.data.body.msg_content):i.error&&i.error(c(e.body.err_code),e.header.seq,i.data.body.msg_category,i.data.body.msg_type,i.data.body.msg_content),delete t.sendDataMap[e.header.seq],t.sendDataList.remove(r)):t.logger.error("hsrmr.0 no found seq="+e.header.seq);t.logger.debug("hsrmr.0 call success")}(t,i);break;case"push_im_chat":!function(t,e){t.onRecvRoomMsg(e.body.chat_data,e.body.server_msg_id,e.body.ret_msg_id)}(t,i);break;case"push_userlist_update":!function(t,e){if(t.logger.debug("hpus.0 call server user seq "+e.body.user_list_seq+" userSeq "+t.userSeq),t.runState!=q.login)return void t.logger.info("hpus.0 not login");if(!t.userStateUpdate)return void t.logger.info("hpus.0 no userStateUpdate flag");if(t.userSeq+e.body.user_actions.length!==e.body.user_list_seq)return t.logger.info("hpus.0 fetch new userlist "+t.userSeq,NaN+e.body.user_list_seq),void R(t);t.userSeq=e.body.user_list_seq,t.logger.debug("hpus.0 push userSeq "+t.userSeq);for(var i=[],r=0;r<e.body.user_actions.length;r++){var s={action:e.body.user_actions[r].Action,idName:e.body.user_actions[r].IdName,nickName:e.body.user_actions[r].NickName,role:e.body.user_actions[r].Role,loginTime:e.body.user_actions[r].LoginTime};i.push(s)}t.onUserStateUpdate(e.body.room_id,i),t.logger.debug("hpus.0 call success")}(t,i);break;case"user_list":!function(t,e){if(t.logger.debug("hfulr.0 call"),0!=e.body.err_code)return t.userQuerying=!1,void t.logger.info("hfulr.0 fetch error "+e.body.err_code);if(!t.userStateUpdate)return;t.userTempList.push.apply(t.userTempList,e.body.user_baseinfos);var i=e.body.ret_user_index,r=e.body.server_user_index;if(i!=r)return t.logger.info("hfulr.0 fetch another page"),void A(i+1);t.userSeq=e.body.server_user_seq,t.logger.info("hfulr.0 set user Seq "+t.userSeq);for(var s=[],o=0;o<t.userTempList.length;o++){var n={idName:t.userTempList[o].id_name,nickName:t.userTempList[o].nick_name,role:t.userTempList[o].role};s.push(n)}t.userQuerying=!1,t.onGetTotalUserList(t.roomid,s),t.userTempList=[],t.logger.debug("hfulr.0 call success user_list "+s+" count "+s.length)}(t,i)}else t.logger.info("ws.bwsh.0 check session fail.");else!function(t,e){if(t.logger.debug("hlr.0 call"),t.runState!==q.trylogin)return void t.logger.info("hlr.0 state error");if(e.header.seq!==t.cmdSeq)return void t.logger.info("hlr.0 in wrong seq, local=",t.cmdSeq,",recv=",e.header.seq);if(0!==e.body.err_code)return function(t,e){if(t.logger.debug("hlf.0 call"),function(t){switch(e.body.err_code){case 1002:case 1003:return!0;default:return!1}}())return void t.logger.info("hlf.0 KeepTry true");var i=t.lastRunState;p(t,q.logout),E(t);var r=c(e.body.err_code);i==q.login?(t.logger.info("hlf.0 callback disconnect"),t.onDisconnect(r)):(t.logger.info("hlf.0 callback error"),u(t,"login")(r));t.logger.debug("hlf.0 call success")}(t,e),void t.logger.info("hlr.0 server error=",e.body.err_code);(function(t,e){t.logger.debug("hls.0 call");var i=t.lastRunState;p(t,q.login),t.userid=e.body.user_id,t.sessionid=e.body.session_id,t.logger.setSessionInfo(t.appid,t.roomid,t.userid,t.idName,t.sessionid),void 0!=e.body.config_info&&(t.logger.setRemoteLogLevel(e.body.config_info.log_level),""!=e.body.config_info.log_url&&t.logger.openLogServer(e.body.config_info.log_url));w(t),v(t),t.heartbeatInterval=e.body.hearbeat_interval,t.heartbeatInterval<W&&(t.heartbeatInterval=W);if(t.heartbeatTimer=setTimeout(function(){T(t)},t.heartbeatInterval),y(t),t.sendDataCheckTimer=setTimeout(function(){b(t)},t.sendDataCheckInterval),t.streamQuerying=!1,i==q.login)t.logger.info("hls.0 recover from disconnect so call streamupdate"),S(t,e.body.stream_seq,e.body.stream_info||[]);else{t.logger.info("hls.0 success callback user"),t.streamList=e.body.stream_info||[],t.streamSeq=e.body.stream_seq;var r=[];r=k(t.streamList),function(t,e){return t.callbackList[e+"SuccessCallback"]}(t,"login")(r)}t.logger.debug("hls.0 userStateUpdate "+t.userStateUpdate),t.userStateUpdate&&(t.logger.info("hls.0 fetch all new userlist"),R(t));t.logger.debug("hls.0 call success")})(t,e),t.logger.info("hlr.0 call success.")}(t,i)},t.websocket.onclose=function(e){t.logger.info("ws.oc.0 msg="+JSON.stringify(e)),t.runState!==q.logout?t.runState===q.trylogin&&t.tryLoginCount<=Y?t.logger.info("ws.oc.0 is called because of try login"):t.runState===q.login?(t.logger.info("ws.oc.0 is called because of network broken, try again"),p(t,q.trylogin),w(t),C(t)):(t.logger.info("ws.oc.0 out of think!!!"),p(t,q.logout),E(t),t.onDisconnect(Q.UNKNOWN)):t.logger.info("ws.oc.0 onclose logout flow call websocket.close")},t.websocket.onerror=function(e){t.logger.info("ws.oe.0 msg="+JSON.stringify(e))}}(t),t.logger.info("tl.0 websocket.onpen send login");var e=_(t);f(t,"login",e),t.logger.debug("tl.0 websocket.onpen call success")}}catch(e){t.logger.error("tl.0 websocket err:"+e)}}t.tryLoginTimer=setTimeout(function(){C(t)},z[t.tryLoginCount%Y]),t.logger.debug("tl.0 call success")}else t.logger.info("tl.0 state error")}function _(t){return{id_name:t.idName,nick_name:t.nickName,role:t.role,token:t.token,version:X,user_state_flag:t.userStateUpdate?1:0}}function E(t){if(t.logger.debug("rr.0 call"),w(t),v(t),y(t),t.streamList=[],t.streamQuerying=!1,t.mapStreamDom={},t.mapStreamQuality={},t.preferPlaySourceType=O.cdn,t.logger.debug("rr.0 call send logout=",t.sessionid),"0"!==t.sessionid){f(t,"logout",{reserve:0})}t.websocket&&(t.websocket.onclose=null,t.websocket.onerror=null,t.websocket.close(),t.websocket=null),t.userid="0",t.sessionid="0",t.logger.setSessionInfo(t.appid,t.roomid,t.userid,t.idName,t.sessionid),t.logger.debug("rr.0 call success")}function x(t){if(t.logger.debug("fsl.0 call"),t.runState===q.login)if(t.streamQuerying)t.logger.info("fsl.0 already doing");else{t.streamQuerying=!0,t.logger.debug("fsl.0 send fetch request");f(t,"stream_info",{reserve:0}),t.logger.debug("fsl.0 call success")}else t.logger.info("fsl.0 state error")}function R(t){t.logger.debug("ful.0 call"),t.userQuerying?t.logger.info("ful.0 is already querying"):(t.userQuerying=!0,t.userTempList=[],A(t,0),t.logger.debug("ful.0 the first time call"))}function A(t,e){t.logger.debug("fulwp.0 call");f(t,"user_list",{user_index:e,sort_type:0}),t.logger.debug("fulwp.0 call success")}function S(t,e,i){t.logger.debug("hfus.0 call"),t.streamSeq=e,function(t,e,i,r){t.logger.debug("msl.0 call");for(var s,o=[],n=[],a=[],h=0;h<i.length;h++){s=!1;for(var l=0;l<e.length;l++)if(i[h].stream_id===e[l].stream_id){i[h].extra_info!==e[l].extra_info&&a.push(i[h]),s=!0;break}s||o.push(i[h])}for(var d=0;d<e.length;d++){s=!1;for(var u=0;u<i.length;u++)if(e[d].stream_id===i[u].stream_id){s=!0;break}s||n.push(e[d])}e=i,r(o,n,a),t.logger.debug("msl.0 call success")}(t,t.streamList,i,function(e,i,r){0!==e.length&&(t.logger.debug("hfus.0 callback addstream"),t.onStreamUpdated(V.added,k(e))),0!==i.length&&(t.logger.debug("hfus.0 callback delstream"),t.onStreamUpdated(V.deleted,k(i))),0!==r.length&&(t.logger.debug("hfus.0 callback updatestream"),t.onStreamExtraInfoUpdated(k(r)))}),t.logger.debug("hfus.0 call success")}function k(t){var e=[];if(void 0!=t&&null!=t)for(var i=0;i<t.length;i++)e.push({anchor_id_name:t[i].anchor_id_name,stream_gid:t[i].stream_gid,anchor_nick_name:t[i].anchor_nick_name,extra_info:t[i].extra_info,stream_id:t[i].stream_id});return e}t.prototype={id:function(t){if(null===t||void 0===t)return this._id;if("number"!=typeof t)throw new Error("Id must be an integer.");this._id=t},data:function(t){if(null===t||void 0===t)return this._data;this._data=t},hasNext:function(){return null!==this.next&&null!==this.next.id()},hasPrev:function(){return null!==this.prev&&null!==this.prev.id()}},e.prototype={insertBefore:function(e,i){var r=new t(this._idCounter,i);return r.next=e,r.prev=e.prev,e.prev.next=r,e.prev=r,++this._idCounter,++this._numNodes,r},addLast:function(t){return this.insertBefore(this.end,t)},add:function(t){return this.addLast(t)},getFirst:function(){return 0===this._numNodes?null:this.start.next},getLast:function(){return 0===this._numNodes?null:this.end.prev},size:function(){return this._numNodes},getFromFirst:function(t){var e=0,i=this.start.next;if(t>=0)for(;e<t&&null!==i;)i=i.next,++e;else i=null;if(null===i)throw"Index out of bounds.";return i},get:function(t){return 0===t?this.getFirst():t===this._numNodes-1?this.getLast():this.getFromFirst(t)},remove:function(t){return t.prev.next=t.next,t.next.prev=t.prev,--this._numNodes,t},removeFirst:function(){var t=null;return this._numNodes>0&&(t=this.remove(this.start.next)),t},removeLast:function(){var t=null;return this._numNodes>0&&(t=this.remove(this.end.prev)),t},removeAll:function(){this.start.next=this.end,this.end.prev=this.start,this._numNodes=0,this._idCounter=0},each:function(t){for(var e=this.start;e.hasNext();)t(e=e.next)},find:function(t){for(var e=this.start,i=!1,r=null;e.hasNext()&&!i;)t(e=e.next)&&(r=e,i=!0);return r},map:function(t){for(var e=this.start,i=[];e.hasNext();)t(e=e.next)&&i.push(e);return i},push:function(t){return this.addLast(t)},unshift:function(t){this._numNodes>0?this.insertBefore(this.start.next,t):this.insertBefore(this.end,t)},pop:function(){return this.removeLast()},shift:function(){return this.removeFirst()}};var P={debug:0,info:1,warn:2,error:3,report:99,disable:100};i.prototype.setLogLevel=function(t){this.logLevel=t,(this.logLevel<P.debug||this.logLevel>P.report)&&(this.logLevel=P.disable)},i.prototype.setRemoteLogLevel=function(t){this.logRemoteLevel=t,(this.logRemoteLevel<P.debug||this.logRemoteLevel>P.report)&&(this.logRemoteLevel=P.disable)},i.prototype.setSessionInfo=function(t,e,i,r,s){this.appid=t,this.roomid=e,this.sessionid=i,this.userid=r,this.userName=s},i.prototype.openLogServer=function(t){if(this.url!=t){if(this.url=t,this.stopLogServer(),!t)return;this.websocket=new WebSocket(t),this.websocket.onopen=function(t){},this.websocket.onclose=function(t){},this.websocket.onmessage=function(t){},this.websocket.onerror=function(t){console.log("ws发生错误！")}}},i.prototype.stopLogServer=function(){this.websocket&&(this.websocket.onclose=null,this.websocket.onerror=null,this.websocket.close(),this.websocket=null)},i.prototype.RemoteLog=function(t,e){if(""!=this.url)if(null==this.websocket||2==this.websocket.readyState||3==this.websocket.readyState){var i=this.url;this.url="",this.openLogServer(i),this.logCacheSend.length<this.logCacheMax&&this.logCacheSend.push(e)}else if(0==this.websocket.readyState)this.logCacheSend.length<this.logCacheMax&&this.logCacheSend.push(e);else if(1==this.websocket.readyState){if(this.logCacheSend>0){for(var r="",s=0;s<this.logCacheSend.length;s++)r=r+this.logCacheSend[s]+"\n";e=r+e,this.logCacheSend=[]}this.websocket.send(e)}else this.logCacheSend.length<this.logCacheMax&&this.logCacheSend.push(e)},i.prototype.log=function(t,e){if(this.logLevel!==P.disable&&this.logLevel<=t)for(this.logCache.push(e);this.logCache.length>this.logCacheMax;)this.logCache.shift();this.logRemoteLevel!==P.disable&&this.logRemoteLevel<=t&&this.RemoteLog(t,e)},i.prototype.debug=function(){var t=r(this,"debug","".concat([].slice.call(arguments)));this.logLevel!==P.disable&&this.logLevel<=P.debug&&console.debug.apply(console,t),this.log(P.debug,t)},i.prototype.info=function(){var t=r(this,"info","".concat([].slice.call(arguments)));this.logLevel!==P.disable&&this.logLevel<=P.info&&console.info.apply(console,t),this.log(P.info,t)},i.prototype.warn=function(){var t=r(this,"warn","".concat([].slice.call(arguments)));this.logLevel!==P.disable&&this.logLevel<=P.warn&&console.warn.apply(console,t),this.log(P.warn,t)},i.prototype.error=function(){var t=r(this,"error","".concat([].slice.call(arguments)));this.logLevel!==P.disable&&this.logLevel<=P.error&&console.error.apply(console,t),this.log(P.error,t)},i.prototype.report=function(){var t=r(this,"report","".concat([].slice.call(arguments)));this.logLevel!==P.disable&&this.logLevel<=P.report&&console.info.apply(console,t),this.log(P.report,t)};var D=["00","01","02","03","04","05","06","07","08","09"],L={Player:null,Renderer:{},AudioOutput:{},Now:function(){return window.performance?window.performance.now()/1e3:Date.now()/1e3},RendererPool:[],GetRenderer:function(t){var e=null;return this.RendererPool.forEach(function(i){i.key==t&&null!=i.value&&(e=i.value)}),e},SetRenderer:function(t,e){var i={key:t,value:e};this.RendererPool.push(i)},DestroyAllRenderer:function(){console.log("this.RendererPool = ",this.RendererPool),this.RendererPool=[]},DestroySingleRenderer:function(t){console.log("this.RendererPool1 = ",this.RendererPool),console.log("this.key = ",t);for(var e=this.RendererPool.length-1;e>=0;e--)this.RendererPool[e].key==t&&this.RendererPool.splice(e,1);console.log("this.RendererPool2 = ",this.RendererPool)}};L.VideoElement=function(){var t=function(e){var i=e.dataset.url;if(!i)throw"VideoElement has no `data-url` attribute";var r=function(t,e){for(var i in e)t.style[i]=e[i]};this.container=e,r(this.container,{display:"inline-block",position:"relative",minWidth:"80px",minHeight:"80px"}),this.canvas=document.createElement("canvas"),this.canvas.width=960,this.canvas.height=540,r(this.canvas,{display:"block",width:"100%"}),this.container.appendChild(this.canvas),this.playButton=document.createElement("div"),this.playButton.innerHTML=t.PLAY_BUTTON,r(this.playButton,{zIndex:2,position:"absolute",top:"0",bottom:"0",left:"0",right:"0",maxWidth:"75px",maxHeight:"75px",margin:"auto",opacity:"0.7",cursor:"pointer"}),this.container.appendChild(this.playButton);var s={canvas:this.canvas};for(var o in e.dataset)try{s[o]=JSON.parse(e.dataset[o])}catch(t){s[o]=e.dataset[o]}if(this.player=new L.Player(i,s),e.playerInstance=this.player,!s.poster||s.autoplay||this.player.options.streaming||(s.decodeFirstFrame=!1,this.poster=new Image,this.poster.src=s.poster,this.poster.addEventListener("load",this.posterLoaded),r(this.poster,{display:"block",zIndex:1,position:"absolute",top:0,left:0,bottom:0,right:0}),this.container.appendChild(this.poster)),this.player.options.streaming||this.container.addEventListener("click",this.onClick.bind(this)),(s.autoplay||this.player.options.streaming)&&(this.playButton.style.display="none"),this.player.audioOut&&!this.player.audioOut.unlocked){var n=this.container;(s.autoplay||this.player.options.streaming)&&(this.unmuteButton=document.createElement("div"),this.unmuteButton.innerHTML=t.UNMUTE_BUTTON,r(this.unmuteButton,{zIndex:2,position:"absolute",bottom:"10px",right:"20px",width:"75px",height:"75px",margin:"auto",opacity:"0.7",cursor:"pointer"}),this.container.appendChild(this.unmuteButton),n=this.unmuteButton),this.unlockAudioBound=this.onUnlockAudio.bind(this,n),n.addEventListener("touchstart",this.unlockAudioBound,!1),n.addEventListener("click",this.unlockAudioBound,!0)}};return t.prototype.onUnlockAudio=function(t,e){this.unmuteButton&&(e.preventDefault(),e.stopPropagation()),this.player.audioOut.unlock(function(){this.unmuteButton&&(this.unmuteButton.style.display="none"),t.removeEventListener("touchstart",this.unlockAudioBound),t.removeEventListener("click",this.unlockAudioBound)}.bind(this))},t.prototype.onClick=function(t){this.player.isPlaying?(this.player.pause(),this.playButton.style.display="block"):(this.player.play(),this.playButton.style.display="none",this.poster&&(this.poster.style.display="none"))},t.PLAY_BUTTON='<svg style="max-width: 75px; max-height: 75px;" viewBox="0 0 200 200" alt="Play video"><circle cx="100" cy="100" r="90" fill="none" stroke-width="15" stroke="#fff"/><polygon points="70, 55 70, 145 145, 100" fill="#fff"/></svg>',t.UNMUTE_BUTTON='<svg style="max-width: 75px; max-height: 75px;" viewBox="0 0 75 75"><polygon class="audio-speaker" stroke="none" fill="#fff" points="39,13 22,28 6,28 6,47 21,47 39,62 39,13"/><g stroke="#fff" stroke-width="5"><path d="M 49,50 69,26"/><path d="M 69,50 49,26"/></g></svg>',t}(),L.Player=function(){var t=function(t,e){this.options=e||{},this.autoplay=!0,e.worker?this.worker=e.worker:e.workerPlayerJSBlob?this.worker=new Worker(window.URL.createObjectURL(e.workerPlayerJSBlob)):e.workerPlayerJS?this.worker=new Worker(e.workerPlayerJS):this.worker=new Worker("jsmpeg-stub.min.js"),this.worker.onmessage=this.onMessage.bind(this),!1!==e.video&&(e.canvas&&(this.renderer=L.GetRenderer(e.canvas.id)),null==this.renderer&&(this.isSupported=L.Renderer.WebGL.IsSupported(),this.renderer=!e.disableGl&&this.isSupported?new L.Renderer.WebGL(e):new L.Renderer.Canvas2D(e),L.SetRenderer(e.canvas.id,this.renderer))),!1!==e.audio&&L.AudioOutput.WebAudio.IsSupported()&&(this.audioOut=new L.AudioOutput.WebAudio(e)),Object.defineProperty(this,"playoutStatus",{get:this.getPlayoutStatus}),this.videoBreakCnt=0,this.unpauseOnShow=!1,!1!==e.pauseWhenHidden&&(this.showHandle=this.showHide.bind(this),document.addEventListener("visibilitychange",this.showHandle,!1)),this.worker.postMessage([0,t]),this.autoplay&&this.play(),this.videoBytes=0,this.videoFrameCnt=0,this.videoDecodeFrameCnt=0,this.videoDecodeTime=0,this.videoBreakCnt=0};return t.prototype.showHide=function(t){"hidden"===document.visibilityState?(this.unpauseOnShow=this.wantsToPlay,this.pause()):this.unpauseOnShow&&this.play()},t.prototype.play=function(t){this.animationId=requestAnimationFrame(this.update.bind(this)),this.wantsToPlay=!0,this.worker.postMessage([1])},t.prototype.pause=function(t){cancelAnimationFrame(this.animationId),this.wantsToPlay=!1,this.isPlaying=!1,this.worker.postMessage([2])},t.prototype.stop=function(t){this.pause()},t.prototype.destroy=function(){this.worker.postMessage([7]),this.worker.onmessage=null,this.worker.terminate(),this.worker=null,this.renderer&&(this.renderer=null),this.audioOut&&(this.audioOut.destroy(),this.audioOut=null),this.options=null,this.showHandle&&(document.removeEventListener("visibilitychange",this.showHandle),this.showHandle=null)},t.prototype.update=function(){this.animationId=requestAnimationFrame(this.update.bind(this)),this.isPlaying||(this.isPlaying=!0,this.startTime=L.Now()-this.currentTime),this.worker.postMessage([4,L.Now()])},t.prototype.getPlayoutStatus=function(){this.worker.postMessage([5]);var t={};return t.videoBytes=this.videoBytes,t.videoFrameCnt=this.videoFrameCnt,t.videoDecodeFrameCnt=this.videoDecodeFrameCnt,t.videoDecodeTime=this.videoDecodeTime,t.videoBreakCnt=this.videoBreakCnt,t.useWebGL=this.isSupported,t},t.prototype.enableDecode=function(t){this.worker.postMessage([6,t])},t.prototype.onMessage=function(t){switch(t.data[0]){case 0:this.renderer.resize(t.data[1],t.data[2]);break;case 1:this.renderer.render(t.data[1],t.data[2],t.data[3]);break;case 2:this.videoBytes=t.data[1],this.videoFrameCnt=t.data[2],this.videoDecodeFrameCnt=t.data[3],this.videoDecodeTime=t.data[4],this.videoBreakCnt=t.data[5]}},t}(),L.Renderer.WebGL=function(){var t=function(e){this.canvas=e.canvas,this.canvas.width=this.canvas.clientWidth,this.canvas.height=this.canvas.clientHeight,this.width=this.canvas.width,this.height=this.canvas.height,this.fixWidth=this.width,this.enabled=!0,this._error=void 0,Object.defineProperty(this,"error",{get:this.getRenderError});var i={preserveDrawingBuffer:!!e.preserveDrawingBuffer,alpha:!1,depth:!1,stencil:!1,antialias:!1};if(this.gl=this.canvas.getContext("webgl",i)||this.canvas.getContext("experimental-webgl",i),!this.gl)throw new Error("Failed to get WebGL Context");var r=this.gl;try{this.bufferConvertVertex=r.createBuffer();var s=new Float32Array([0,0,0,1,1,0,1,1]);r.bindBuffer(r.ARRAY_BUFFER,this.bufferConvertVertex),r.bufferData(r.ARRAY_BUFFER,s,r.STATIC_DRAW),r.bindBuffer(r.ARRAY_BUFFER,null),this.convertProgram=this.createProgram(t.SHADER.VERTEX_IDENTITY,t.SHADER.FRAGMENT_YCRCB_TO_RGBA),this.attrConvertVertex=r.getAttribLocation(this.convertProgram,"vertex"),this.uniformY=r.getUniformLocation(this.convertProgram,"textureY"),this.uniformCb=r.getUniformLocation(this.convertProgram,"textureCb"),this.uniformCr=r.getUniformLocation(this.convertProgram,"textureCr"),this.textureY=this.createTexture(0,"textureY",this.convertProgram),this.textureCb=this.createTexture(1,"textureCb",this.convertProgram),this.textureCr=this.createTexture(2,"textureCr",this.convertProgram),this.loadingProgram=this.createProgram(t.SHADER.VERTEX_IDENTITY,t.SHADER.FRAGMENT_LOADING),this.bufferBlitVertex=r.createBuffer(),this.bufferBlitTexCoord=r.createBuffer(),this.blitProgram=this.createProgram(t.SHADER.VERTEX_BLIT,t.SHADER.FRAGMENT_BLIT),this.attrBlitVertex=r.getAttribLocation(this.blitProgram,"vertex"),this.attrBlitTexCoord=r.getAttribLocation(this.blitProgram,"texCoord"),this.uniformRgb=r.getUniformLocation(this.blitProgram,"textureRgb"),this.fb=r.createFramebuffer()}catch(t){this._error=t}this.shouldCreateUnclampedViews=!this.allowsClampedTextureData(),this.viewMode=e.viewMode||0};return t.prototype.destroy=function(){var t=this.gl;t.deleteTexture(this.textureY),t.deleteTexture(this.textureCb),t.deleteTexture(this.textureCr),void 0!==this.textureRgb&&(t.deleteTexture(this.textureRgb),this.textureRgb=void 0),t.deleteProgram(this.convertProgram),t.deleteProgram(this.loadingProgram),t.deleteProgram(this.blitProgram),t.deleteBuffer(this.bufferConvertVertex),t.deleteBuffer(this.bufferBlitVertex),t.deleteBuffer(this.bufferBlitTexCoord),t.deleteFramebuffer(this.fb)},t.prototype.resize=function(t,e){if(void 0===this._error){this.width=0|t,this.height=0|e,this.fixWidth=this.width+15>>4<<4,console.info("width:",t),console.info("height:",e);var i=this.gl;i.bindFramebuffer(i.FRAMEBUFFER,this.fb),void 0!==this.textureRgb&&(i.deleteTexture(this.textureRgb),this.textureRgb=void 0),this.textureRgb=this.createTexture(3,"textureRgb",this.blitProgram),i.texImage2D(i.TEXTURE_2D,0,i.RGB,this.fixWidth,this.height,0,i.RGB,i.UNSIGNED_BYTE,null),i.framebufferTexture2D(i.FRAMEBUFFER,i.COLOR_ATTACHMENT0,i.TEXTURE_2D,this.textureRgb,0);var r=1,s=1,o=this.width/this.height,n=this.canvas.width/this.canvas.height;0===this.viewMode?o>n?s=this.canvas.width/o/this.canvas.height:r=this.canvas.height*o/this.canvas.width:1===this.viewMode?o>n?r=this.canvas.height*o/this.canvas.width:s=this.canvas.width/o/this.canvas.height:this.viewMode,i.bindBuffer(i.ARRAY_BUFFER,this.bufferBlitVertex);var a=new Float32Array([-1*r,-1*s,r,-1*s,-1*r,s,r,s]);i.bufferData(i.ARRAY_BUFFER,a,i.STATIC_DRAW),i.bindBuffer(i.ARRAY_BUFFER,null);var h=this.width/this.fixWidth;i.bindBuffer(i.ARRAY_BUFFER,this.bufferBlitTexCoord);var l=new Float32Array([0,0,h,0,0,1,h,1]);i.bufferData(i.ARRAY_BUFFER,l,i.STATIC_DRAW),i.bindBuffer(i.ARRAY_BUFFER,null)}},t.prototype.createTexture=function(t,e,i){var r=this.gl,s=r.createTexture();return r.bindTexture(r.TEXTURE_2D,s),r.texParameteri(r.TEXTURE_2D,r.TEXTURE_MAG_FILTER,r.LINEAR),r.texParameteri(r.TEXTURE_2D,r.TEXTURE_MIN_FILTER,r.LINEAR),r.texParameteri(r.TEXTURE_2D,r.TEXTURE_WRAP_S,r.CLAMP_TO_EDGE),r.texParameteri(r.TEXTURE_2D,r.TEXTURE_WRAP_T,r.CLAMP_TO_EDGE),s},t.prototype.createProgram=function(t,e){var i=this.gl,r=i.createProgram();return i.attachShader(r,this.compileShader(i.VERTEX_SHADER,t)),i.attachShader(r,this.compileShader(i.FRAGMENT_SHADER,e)),i.linkProgram(r),i.useProgram(r),r},t.prototype.compileShader=function(t,e){var i=this.gl,r=i.createShader(t);if(i.shaderSource(r,e),i.compileShader(r),!i.getShaderParameter(r,i.COMPILE_STATUS))throw new Error(i.getShaderInfoLog(r));return r},t.prototype.allowsClampedTextureData=function(){var t=this.gl,e=t.createTexture();return t.bindTexture(t.TEXTURE_2D,e),t.texImage2D(t.TEXTURE_2D,0,t.LUMINANCE,1,1,0,t.LUMINANCE,t.UNSIGNED_BYTE,new Uint8ClampedArray([0])),0===t.getError()},t.prototype.renderProgress=function(t){return},t.prototype.render=function(t,e,i){if(this.enabled&&void 0===this._error){var r=this.gl,s=this.fixWidth,o=this.height,n=s>>1,a=o>>1;r.bindFramebuffer(r.FRAMEBUFFER,this.fb),r.viewport(0,0,this.fixWidth,this.height),r.clearColor(0,0,0,1),r.clear(r.COLOR_BUFFER_BIT),r.useProgram(this.convertProgram),this.shouldCreateUnclampedViews&&(t=new Uint8Array(t.buffer),e=new Uint8Array(e.buffer),i=new Uint8Array(i.buffer)),this.updateTexture(r.TEXTURE0,this.textureY,s,o,t),this.updateTexture(r.TEXTURE1,this.textureCb,n,a,e),this.updateTexture(r.TEXTURE2,this.textureCr,n,a,i),r.uniform1i(this.uniformY,0),r.uniform1i(this.uniformCb,1),r.uniform1i(this.uniformCr,2),r.bindBuffer(r.ARRAY_BUFFER,this.bufferConvertVertex),r.enableVertexAttribArray(this.attrConvertVertex),r.vertexAttribPointer(this.attrConvertVertex,2,r.FLOAT,!1,0,0),r.drawArrays(r.TRIANGLE_STRIP,0,4),r.bindBuffer(r.ARRAY_BUFFER,null),r.bindFramebuffer(r.FRAMEBUFFER,null),r.viewport(0,0,this.canvas.width,this.canvas.height),r.clearColor(0,0,0,1),r.clear(r.COLOR_BUFFER_BIT),r.useProgram(this.blitProgram),r.activeTexture(r.TEXTURE3),r.bindTexture(r.TEXTURE_2D,this.textureRgb),r.uniform1i(this.uniformRgb,3),r.bindBuffer(r.ARRAY_BUFFER,this.bufferBlitVertex),r.enableVertexAttribArray(this.attrBlitVertex),r.vertexAttribPointer(this.attrBlitVertex,2,r.FLOAT,!1,0,0),r.bindBuffer(r.ARRAY_BUFFER,this.bufferBlitTexCoord),r.enableVertexAttribArray(this.attrBlitTexCoord),r.vertexAttribPointer(this.attrBlitTexCoord,2,r.FLOAT,!1,0,0),r.drawArrays(r.TRIANGLE_STRIP,0,4),r.bindBuffer(r.ARRAY_BUFFER,null)}},t.prototype.updateTexture=function(t,e,i,r,s){var o=this.gl;o.activeTexture(t),o.bindTexture(o.TEXTURE_2D,e),o.texImage2D(o.TEXTURE_2D,0,o.LUMINANCE,i,r,0,o.LUMINANCE,o.UNSIGNED_BYTE,s)},t.prototype.getRenderError=function(){return this._error},t.prototype.enable=function(t){this.enabled=t},t.IsSupported=function(){try{if(!window.WebGLRenderingContext)return!1;var t=document.createElement("canvas");return!(!t.getContext("webgl")&&!t.getContext("experimental-webgl"))}catch(t){return!1}},t.SHADER={FRAGMENT_YCRCB_TO_RGBA:["precision mediump float;","uniform sampler2D textureY;","uniform sampler2D textureCb;","uniform sampler2D textureCr;","varying vec2 texCoord;","mat4 rec601 = mat4(","1.16438,  0.00000,  1.59603, -0.87079,","1.16438, -0.39176, -0.81297,  0.52959,","1.16438,  2.01723,  0.00000, -1.08139,","0, 0, 0, 1",");","void main() {","float y = texture2D(textureY, texCoord).r;","float cb = texture2D(textureCb, texCoord).r;","float cr = texture2D(textureCr, texCoord).r;","gl_FragColor = vec4(y, cr, cb, 1.0) * rec601;","}"].join("\n"),FRAGMENT_LOADING:["precision mediump float;","uniform float progress;","varying vec2 texCoord;","void main() {","float c = ceil(progress-(1.0-texCoord.y));","gl_FragColor = vec4(c,c,c,1);","}"].join("\n"),VERTEX_IDENTITY:["attribute vec2 vertex;","varying vec2 texCoord;","void main() {","texCoord = vertex;","gl_Position = vec4((vertex * 2.0 - 1.0) * vec2(1, -1), 0.0, 1.0);","}"].join("\n"),FRAGMENT_BLIT:["precision mediump float;","uniform sampler2D textureRgb;","varying vec2 textureCoordinate;","void main() {","gl_FragColor = texture2D(textureRgb, textureCoordinate);","}"].join("\n"),VERTEX_BLIT:["attribute vec4 vertex;","attribute vec4 texCoord;","varying vec2 textureCoordinate;","void main() {","textureCoordinate = texCoord.xy;","gl_Position = vertex;","}"].join("\n")},t}(),L.Renderer.Canvas2D=function(){var t=function(t){this.canvas=t.canvas||document.createElement("canvas"),this.width=this.canvas.width,this.height=this.canvas.height,this.enabled=!0,Object.defineProperty(this,"error",{get:this.getRenderError});try{this.context=this.canvas.getContext("2d")}catch(t){this._error=t}this.viewMode=t.viewMode||0};return t.prototype.destroy=function(){},t.prototype.resize=function(t,e){this.width=0|t,this.height=0|e;var i=this.width,r=this.height,s=this.width/this.height,o=this.canvas.clientWidth/this.canvas.clientHeight;0===this.viewMode?s<o?i=r*o:r=i/o:1===this.viewMode?s<o?r=i/o:i=r*o:this.viewMode,console.info("imgAR:",s),console.info("viewAR:",o),console.info("viewWidth:",i),console.info("viewHeight:",r),this.canvas.width=i,this.canvas.height=r,this.imageData=this.context.createImageData(this.width,this.height),L.Fill(this.imageData.data,255)},t.prototype.renderProgress=function(t){var e=this.canvas.width,i=this.canvas.height,r=this.context;r.fillStyle="#222",r.fillRect(0,0,e,i),r.fillStyle="#fff",r.fillRect(0,i-i*t,e,i*t)},t.prototype.render=function(t,e,i){this.YCbCrToRGBA(t,e,i,this.imageData.data);var r=0,s=0;0===this.viewMode?(r=(this.canvas.width-this.width)/2,s=(this.canvas.height-this.height)/2,this.context.fillStyle="black",this.context.fillRect(0,0,this.canvas.width,this.canvas.height),this.context.putImageData(this.imageData,r,s,0,0,this.width,this.height)):1===this.viewMode?this.context.putImageData(this.imageData,0,0,0,0,this.canvas.width,this.canvas.height):2===this.viewMode&&this.context.putImageData(this.imageData,0,0)},t.prototype.YCbCrToRGBA=function(t,e,i,r){if(this.enabled)for(var s,o,n,a,h,l=this.width+15>>4<<4,d=l>>1,u=0,c=l,p=l+(l-this.width),g=0,f=d-(this.width>>1),m=0,b=4*this.width,y=4*this.width,v=this.width>>1,T=this.height>>1,w=0;w<T;w++){for(var C=0;C<v;C++){s=e[g],o=i[g],g++,n=s+(103*s>>8)-179,a=(88*o>>8)-44+(183*s>>8)-91,h=o+(198*o>>8)-227;var _=t[u++],E=t[u++];r[m]=_+n,r[m+1]=_-a,r[m+2]=_+h,r[m+4]=E+n,r[m+5]=E-a,r[m+6]=E+h,m+=8;var x=t[c++],R=t[c++];r[b]=x+n,r[b+1]=x-a,r[b+2]=x+h,r[b+4]=R+n,r[b+5]=R-a,r[b+6]=R+h,b+=8}u+=p,c+=p,m+=y,b+=y,g+=f}},t.prototype.getRenderError=function(){return this._error},t.prototype.enable=function(t){this.enabled=t},t}(),L.AudioOutput.WebAudio=function(){var t=function(e){this.context=t.CachedContext=t.CachedContext||new(window.AudioContext||window.webkitAudioContext),this.gain=this.context.createGain(),this.destination=this.gain,this.gain.connect(this.context.destination),this.context._connections=(this.context._connections||0)+1,this.startTime=0,this.buffer=null,this.wallclockStartTime=0,this.volume=1,this.enabled=!0,this.unlocked=!t.NeedsUnlocking(),Object.defineProperty(this,"enqueuedTime",{get:this.getEnqueuedTime})};return t.prototype.destroy=function(){this.gain.disconnect(),this.context._connections--,0===this.context._connections&&(this.context.close(),t.CachedContext=null)},t.prototype.play=function(t,e,i){if(this.enabled){if(!this.unlocked){var r=L.Now();return this.wallclockStartTime<r&&(this.wallclockStartTime=r),void(this.wallclockStartTime+=e.length/t)}this.gain.gain.value=this.volume;var s=this.context.createBuffer(2,e.length,t);s.getChannelData(0).set(e),s.getChannelData(1).set(i);var o=this.context.createBufferSource();o.buffer=s,o.connect(this.destination);var n=this.context.currentTime,a=s.duration;this.startTime<n&&(this.startTime=n,this.wallclockStartTime=L.Now()),o.start(this.startTime),this.startTime+=a,this.wallclockStartTime+=a}},t.prototype.stop=function(){this.gain.gain.value=0},t.prototype.getEnqueuedTime=function(){return Math.max(this.wallclockStartTime-L.Now(),0)},t.prototype.resetEnqueuedTime=function(){this.startTime=this.context.currentTime,this.wallclockStartTime=L.Now()},t.prototype.unlock=function(t){if(this.unlocked)t&&t();else{this.unlockCallback=t;var e=this.context.createBuffer(1,1,22050),i=this.context.createBufferSource();i.buffer=e,i.connect(this.destination),i.start(0),setTimeout(this.checkIfUnlocked.bind(this,i,0),0)}},t.prototype.checkIfUnlocked=function(t,e){t.playbackState===t.PLAYING_STATE||t.playbackState===t.FINISHED_STATE?(this.unlocked=!0,this.unlockCallback&&(this.unlockCallback(),this.unlockCallback=null)):e<10&&setTimeout(this.checkIfUnlocked.bind(this,t,e+1),100)},t.NeedsUnlocking=function(){return/iPhone|iPad|iPod/i.test(navigator.userAgent)},t.IsSupported=function(){return window.AudioContext||window.webkitAudioContext},t.CachedContext=null,t}();var I=document.currentScript.src,B={start:0,playing:1,stop:2};s.prototype.resetPlayer=function(){if(this.player)try{this.player.stop(),this.player.destroy(),this.player=null}catch(t){this.logger.info("zp.rp.0 stop player destroy exception.")}this.state=B.stop,this.stateTimeStamp=Date.now(),this.playerStat={videoBytes:0,videoFrameCnt:0,videoDecodeFrameCnt:0}},s.prototype.newPlayer=function(t){this.resetPlayer();var e,i;return this.workerPlayerJSBlob?i=this.workerPlayerJSBlob:e=this.workerUrl?this.workerUrl:function(t){var e=t.split("/"),i=e[e.length-1];return t.replace(i,"")}(I)+"jsmpeg-stub.min.js",this.logger.info("zp.np.0 worker: ",e),this.player=new L.Player(this.urls[this.playUrlIndex%this.urls.length],{audio:!1,canvas:this.view,viewMode:this.viewMode,workerPlayerJS:e,workerPlayerJSBlob:i}),!!this.player&&(this.state=B.start,!0)},s.prototype.setVolume=function(t){if(this.logger.debug("zp.sv.0 call"),"number"!=typeof t||t<0||t>100)return this.logger.info("zp.sv.0 param error"),!1;this.player.volume(t/100),this.logger.debug("zp.sv.0 call success")},s.prototype.getCurrentPlayerUrl=function(){return this.urls[this.playUrlIndex%this.urls.length]},s.prototype.updatePlayerStat=function(){this.playerStat.videoBytes=this.player.playoutStatus.videoBytes,this.playerStat.videoFrameCnt=this.player.playoutStatus.videoFrameCnt,this.playerStat.videoDecodeFrameCnt=this.player.playoutStatus.videoDecodeFrameCnt},s.prototype.enableDecode=function(t){return this.logger.info("zp.ed.0 call"),"boolean"!=typeof t?(this.logger.info("zp.ed.0 param error"),!1):this.player?(this.decode=t,this.player.enableDecode(t),this.logger.info("zp.ed.0 call success "+t),!0):(this.logger.info("zp.ed.0 don't have player"),!1)};var F={Player:null,VideoElement:null,BitBuffer:null,Source:{},Demuxer:{},Decoder:{},Renderer:{},AudioOutput:{},Now:function(){return window.performance?window.performance.now()/1e3:Date.now()/1e3},CreateVideoElements:function(){for(var t=document.querySelectorAll(".jsmpeg"),e=0;e<t.length;e++)new F.VideoElement(t[e])},Fill:function(t,e){if(t.fill)t.fill(e);else for(var i=0;i<t.length;i++)t[i]=e},RendererPool:[],GetRenderer:function(t){var e=null;return this.RendererPool.forEach(function(i){i&&i.key==t&&null!=i.value&&(e=i.value)}),e},SetRenderer:function(t,e){var i={key:t,value:e};this.RendererPool.push(i)},DestroyAllRenderer:function(){console.log("this.RendererPool = ",this.RendererPool),this.RendererPool=[]},DestroySingleRenderer:function(t){console.log("this.RendererPool1 = ",this.RendererPool),console.log("this.key = ",t);for(var e=this.RendererPool.length-1;e>=0;e--)this.RendererPool[e].key==t&&this.RendererPool.splice(e,1);console.log("this.RendererPool2 = ",this.RendererPool)}};"complete"===document.readyState?F.CreateVideoElements():document.addEventListener("DOMContentLoaded",F.CreateVideoElements),F.VideoElement=function(){var t=function(e){var i=e.dataset.url;if(!i)throw"VideoElement has no `data-url` attribute";var r=function(t,e){for(var i in e)t.style[i]=e[i]};this.container=e,r(this.container,{display:"inline-block",position:"relative",minWidth:"80px",minHeight:"80px"}),this.canvas=document.createElement("canvas"),this.canvas.width=960,this.canvas.height=540,r(this.canvas,{display:"block",width:"100%"}),this.container.appendChild(this.canvas),this.playButton=document.createElement("div"),this.playButton.innerHTML=t.PLAY_BUTTON,r(this.playButton,{zIndex:2,position:"absolute",top:"0",bottom:"0",left:"0",right:"0",maxWidth:"75px",maxHeight:"75px",margin:"auto",opacity:"0.7",cursor:"pointer"}),this.container.appendChild(this.playButton);var s={canvas:this.canvas};for(var o in e.dataset)try{s[o]=JSON.parse(e.dataset[o])}catch(t){s[o]=e.dataset[o]}if(this.player=new F.Player(i,s),e.playerInstance=this.player,!s.poster||s.autoplay||this.player.options.streaming||(s.decodeFirstFrame=!1,this.poster=new Image,this.poster.src=s.poster,this.poster.addEventListener("load",this.posterLoaded),r(this.poster,{display:"block",zIndex:1,position:"absolute",top:0,left:0,bottom:0,right:0}),this.container.appendChild(this.poster)),this.player.options.streaming||this.container.addEventListener("click",this.onClick.bind(this)),(s.autoplay||this.player.options.streaming)&&(this.playButton.style.display="none"),this.player.audioOut&&!this.player.audioOut.unlocked){var n=this.container;(s.autoplay||this.player.options.streaming)&&(this.unmuteButton=document.createElement("div"),this.unmuteButton.innerHTML=t.UNMUTE_BUTTON,r(this.unmuteButton,{zIndex:2,position:"absolute",bottom:"10px",right:"20px",width:"75px",height:"75px",margin:"auto",opacity:"0.7",cursor:"pointer"}),this.container.appendChild(this.unmuteButton),n=this.unmuteButton),this.unlockAudioBound=this.onUnlockAudio.bind(this,n),n.addEventListener("touchstart",this.unlockAudioBound,!1),n.addEventListener("click",this.unlockAudioBound,!0)}};return t.prototype.onUnlockAudio=function(t,e){this.unmuteButton&&(e.preventDefault(),e.stopPropagation()),this.player.audioOut.unlock(function(){this.unmuteButton&&(this.unmuteButton.style.display="none"),t.removeEventListener("touchstart",this.unlockAudioBound),t.removeEventListener("click",this.unlockAudioBound)}.bind(this))},t.prototype.onClick=function(t){this.player.isPlaying?(this.player.pause(),this.playButton.style.display="block"):(this.player.play(),this.playButton.style.display="none",this.poster&&(this.poster.style.display="none"))},t.PLAY_BUTTON='<svg style="max-width: 75px; max-height: 75px;" viewBox="0 0 200 200" alt="Play video"><circle cx="100" cy="100" r="90" fill="none" stroke-width="15" stroke="#fff"/><polygon points="70, 55 70, 145 145, 100" fill="#fff"/></svg>',t.UNMUTE_BUTTON='<svg style="max-width: 75px; max-height: 75px;" viewBox="0 0 75 75"><polygon class="audio-speaker" stroke="none" fill="#fff" points="39,13 22,28 6,28 6,47 21,47 39,62 39,13"/><g stroke="#fff" stroke-width="5"><path d="M 49,50 69,26"/><path d="M 69,50 49,26"/></g></svg>',t}(),F.Player=function(){var t=function(t,e){this.options=e||{},e.source?(this.source=new e.source(t,e),e.streaming=!!this.source.streaming):t.match(/^wss?:\/\//)?(this.source=new F.Source.WebSocket(t,e),e.streaming=!0):!1!==e.progressive?(this.source=new F.Source.AjaxProgressive(t,e),e.streaming=!1):(this.source=new F.Source.Ajax(t,e),e.streaming=!1),this.maxAudioLag=e.maxAudioLag||.25,this.loop=!1!==e.loop,this.autoplay=!!e.autoplay||e.streaming,this.demuxer=new F.Demuxer.TS(e),this.source.connect(this.demuxer),!1!==e.video&&("undefined"!=typeof Worker&&e.enableWorker?(this.useWorker=!0,this.video=new F.Decoder.Proxy(e)):(this.useWorker=!1,this.video=new F.Decoder.MPEG1Video(e)),e.canvas&&(this.renderer=F.GetRenderer(e.canvas.id)),null==this.renderer&&(this.isSupported=F.Renderer.WebGL.IsSupported(),this.renderer=!e.disableGl&&this.isSupported?new F.Renderer.WebGL(e):new F.Renderer.Canvas2D(e),F.SetRenderer(e.canvas.id,this.renderer)),this.demuxer.connect(F.Demuxer.TS.STREAM.VIDEO_1,this.video),this.video.connect(this.renderer)),!1!==e.audio&&F.AudioOutput.WebAudio.IsSupported()&&(this.audio=new F.Decoder.MP2Audio(e),this.audioOut=new F.AudioOutput.WebAudio(e),this.demuxer.connect(F.Demuxer.TS.STREAM.AUDIO_1,this.audio),this.audio.connect(this.audioOut)),Object.defineProperty(this,"currentTime",{get:this.getCurrentTime,set:this.setCurrentTime}),Object.defineProperty(this,"volume",{get:this.getVolume,set:this.setVolume}),Object.defineProperty(this,"playoutStatus",{get:this.getPlayoutStatus}),this.lastDecodeTime=0,this.videoBreakCnt=0,this.animationDuration=0,this.unpauseOnShow=!1,!1!==e.pauseWhenHidden&&(this.showHandle=this.showHide.bind(this),document.addEventListener("visibilitychange",this.showHandle,!1)),this.source.start(),this.autoplay&&this.play()};return t.prototype.showHide=function(t){"hidden"===document.visibilityState?(this.unpauseOnShow=this.wantsToPlay,this.pause()):this.unpauseOnShow&&this.play()},t.prototype.play=function(t){this.animationId=requestAnimationFrame(this.update.bind(this)),this.wantsToPlay=!0},t.prototype.pause=function(t){cancelAnimationFrame(this.animationId),this.wantsToPlay=!1,this.isPlaying=!1,this.audio&&this.audio.canPlay&&(this.audioOut.stop(),this.seek(this.currentTime))},t.prototype.getVolume=function(){return this.audioOut?this.audioOut.volume:0},t.prototype.setVolume=function(t){this.audioOut&&(this.audioOut.volume=t)},t.prototype.stop=function(t){this.pause(),this.seek(0),this.video&&!1!==this.options.decodeFirstFrame&&this.video.decode()},t.prototype.destroy=function(){this.pause(),this.source.destroy(),this.source=null,this.demuxer.destroy(),this.demuxer=null,this.video&&(this.video.destroy(),this.video=null),this.renderer&&(this.renderer=null),this.audio&&(this.audio.destroy(),this.audio=null),this.audioOut&&(this.audioOut.destroy(),this.audioOut=null),this.options=null,this.showHandle&&(document.removeEventListener("visibilitychange",this.showHandle),this.showHandle=null)},t.prototype.seek=function(t){var e=this.audio&&this.audio.canPlay?this.audio.startTime:this.video.startTime;this.video&&this.video.seek(t+e),this.audio&&this.audio.seek(t+e),this.startTime=F.Now()-t},t.prototype.getCurrentTime=function(){return this.audio&&this.audio.canPlay?this.audio.currentTime-this.audio.startTime:this.video.currentTime-this.video.startTime},t.prototype.setCurrentTime=function(t){this.seek(t)},t.prototype.update=function(){this.animationId=requestAnimationFrame(this.update.bind(this)),!this.source||this.source.established?(this.isPlaying||(this.isPlaying=!0,this.startTime=F.Now()-this.currentTime),this.options.streaming?this.updateForStreaming():this.updateForStaticFile()):this.renderer&&this.renderer.renderProgress(this.source.progress)},t.prototype.updateForStreaming=function(){var t=F.Now();if(this.video&&(this.video.decode()?(this.lastDecodeTime>0&&(this.animationDuration=t-this.lastDecodeTime),this.lastDecodeTime=t):t-this.lastDecodeTime>500&&this.videoBreakCnt++),this.audio){var e=!1;do{this.audioOut.enqueuedTime>this.maxAudioLag&&(this.audioOut.resetEnqueuedTime(),this.audioOut.enabled=!1),e=this.audio.decode()}while(e);this.audioOut.enabled=!0}},t.prototype.updateForStaticFile=function(){var t=!1,e=0;if(this.audio&&this.audio.canPlay){for(;!t&&this.audio.decodedTime-this.audio.currentTime<.25;)t=!this.audio.decode();this.video&&this.video.currentTime<this.audio.currentTime&&(t=!this.video.decode()),e=this.demuxer.currentTime-this.audio.currentTime}else if(this.video){var i=F.Now()-this.startTime+this.video.startTime,r=i-this.video.currentTime,s=1/this.video.frameRate;this.video&&r>0&&(r>2*s&&(this.startTime+=r),t=!this.video.decode()),e=this.demuxer.currentTime-i}this.source.resume(e),t&&this.source.completed&&(this.loop?this.seek(0):this.pause())},t.prototype.getPlayoutStatus=function(){var t={};return t.videoBytes=this.demuxer.videoBytes,t.videoFrameCnt=this.demuxer.videoFrameCnt,t.videoDecodeFrameCnt=this.video.frameCnt,t.videoBreakCnt=this.videoBreakCnt,t.useWebGL=this.isSupported,t.useWorker=this.useWorker,t.animationDuration=this.animationDuration,t},t.prototype.enableRender=function(t){this.renderer.enable(t)},t.prototype.enableDecode=function(t){this.video.setEnable(t)},t}(),F.BitBuffer=function(){var t=function(e,i){"object"==typeof e?(this.bytes=e instanceof Uint8Array?e:new Uint8Array(e),this.byteLength=this.bytes.length):(this.bytes=new Uint8Array(e||1048576),this.byteLength=0),this.mode=i||t.MODE.EXPAND,this.index=0};return t.prototype.resize=function(t){var e=new Uint8Array(t);0!==this.byteLength&&(this.byteLength=Math.min(this.byteLength,t),e.set(this.bytes,0,this.byteLength)),this.bytes=e,this.index=Math.min(this.index,this.byteLength<<3)},t.prototype.evict=function(t){var e=this.index>>3,i=this.bytes.length-this.byteLength;if(this.index===this.byteLength<<3||t>i+e)return this.byteLength=0,void(this.index=0);0!==e&&(this.bytes.copyWithin?this.bytes.copyWithin(0,e,this.byteLength):this.bytes.set(this.bytes.subarray(e,this.byteLength)),this.byteLength=this.byteLength-e,this.index-=e<<3)},t.prototype.write=function(e){var i="object"==typeof e[0],r=0,s=this.bytes.length-this.byteLength;if(i)for(var r=0,o=0;o<e.length;o++)r+=e[o].byteLength;else r=e.byteLength;if(r>s)if(this.mode===t.MODE.EXPAND){var n=Math.max(2*this.bytes.length,r-s);this.resize(n)}else this.evict(r);if(i)for(o=0;o<e.length;o++)this.appendSingleBuffer(e[o]);else this.appendSingleBuffer(e)},t.prototype.appendSingleBuffer=function(t){t=t instanceof Uint8Array?t:new Uint8Array(t),this.bytes.set(t,this.byteLength),this.byteLength+=t.length},t.prototype.findNextStartCode=function(){for(var t=this.index+7>>3;t<this.byteLength;t++)if(0==this.bytes[t]&&0==this.bytes[t+1]&&1==this.bytes[t+2])return this.index=t+4<<3,this.bytes[t+3];return this.index=this.byteLength<<3,-1},t.prototype.findStartCode=function(t){for(var e=0;;)if((e=this.findNextStartCode())===t||-1===e)return e;return-1},t.prototype.nextBytesAreStartCode=function(){var t=this.index+7>>3;return t>=this.byteLength||0==this.bytes[t]&&0==this.bytes[t+1]&&1==this.bytes[t+2]},t.prototype.peek=function(t){for(var e=this.index,i=0;t;){var r=this.bytes[e>>3],s=8-(7&e),o=s<t?s:t,n=s-o;i=i<<o|(r&255>>8-o<<n)>>n,e+=o,t-=o}return i},t.prototype.read=function(t){var e=this.peek(t);return this.index+=t,e},t.prototype.skip=function(t){return this.index+=t},t.prototype.rewind=function(t){this.index=Math.max(this.index-t,0)},t.prototype.has=function(t){return(this.byteLength<<3)-this.index>=t},t.MODE={EVICT:1,EXPAND:2},t}(),F.Source.Ajax=function(){var t=function(t,e){this.url=t,this.destination=null,this.request=null,this.completed=!1,this.established=!1,this.progress=0};return t.prototype.connect=function(t){this.destination=t},t.prototype.start=function(){this.request=new XMLHttpRequest,this.request.onreadystatechange=function(){this.request.readyState===this.request.DONE&&200===this.request.status&&this.onLoad(this.request.response)}.bind(this),this.request.onprogress=this.onProgress.bind(this),this.request.open("GET",this.url),this.request.responseType="arraybuffer",this.request.send()},t.prototype.resume=function(t){},t.prototype.destroy=function(){this.request.abort()},t.prototype.onProgress=function(t){this.progress=t.loaded/t.total},t.prototype.onLoad=function(t){this.established=!0,this.completed=!0,this.progress=1,this.destination&&this.destination.write(t)},t}(),F.Source.AjaxProgressive=function(){var t=function(t,e){this.url=t,this.destination=null,this.request=null,this.completed=!1,this.established=!1,this.progress=0,this.fileSize=0,this.loadedSize=0,this.chunkSize=e.chunkSize||1048576,this.isLoading=!1,this.loadStartTime=0,this.throttled=!1!==e.throttled,this.aborted=!1};return t.prototype.connect=function(t){this.destination=t},t.prototype.start=function(){this.request=new XMLHttpRequest,this.request.onreadystatechange=function(){this.request.readyState===this.request.DONE&&(this.fileSize=parseInt(this.request.getResponseHeader("Content-Length")),this.loadNextChunk())}.bind(this),this.request.onprogress=this.onProgress.bind(this),this.request.open("HEAD",this.url),this.request.send()},t.prototype.resume=function(t){if(!this.isLoading&&this.throttled){8*this.loadTime+2>t&&this.loadNextChunk()}},t.prototype.destroy=function(){this.request.abort(),this.aborted=!0},t.prototype.loadNextChunk=function(){var t=this.loadedSize,e=Math.min(this.loadedSize+this.chunkSize-1,this.fileSize-1);t>=this.fileSize||this.aborted?this.completed=!0:(this.isLoading=!0,this.loadStartTime=F.Now(),this.request=new XMLHttpRequest,this.request.onreadystatechange=function(){this.request.readyState===this.request.DONE&&this.request.status>=200&&this.request.status<300?this.onChunkLoad(this.request.response):this.request.readyState===this.request.DONE&&this.loadFails++<3&&this.loadNextChunk()}.bind(this),0===t&&(this.request.onprogress=this.onProgress.bind(this)),this.request.open("GET",this.url+"?"+t+"-"+e),this.request.setRequestHeader("Range","bytes="+t+"-"+e),this.request.responseType="arraybuffer",this.request.send())},t.prototype.onProgress=function(t){this.progress=t.loaded/t.total},t.prototype.onChunkLoad=function(t){this.established=!0,this.progress=1,this.loadedSize+=t.byteLength,this.loadFails=0,this.isLoading=!1,this.destination&&this.destination.write(t),this.loadTime=F.Now()-this.loadStartTime,this.throttled||this.loadNextChunk()},t}(),F.Source.WebSocket=function(){var t=function(t,e){this.url=t,this.options=e,this.socket=null,this.callbacks={connect:[],data:[]},this.destination=null,this.reconnectInterval=void 0!==e.reconnectInterval?e.reconnectInterval:5,this.shouldAttemptReconnect=!!this.reconnectInterval,this.completed=!1,this.established=!1,this.progress=0,this.reconnectTimeoutId=0};return t.prototype.connect=function(t){this.destination=t},t.prototype.destroy=function(){clearTimeout(this.reconnectTimeoutId),this.shouldAttemptReconnect=!1,this.socket.close(),this.socket.onmessage=null,this.socket.onopen=null,this.socket.onerror=null,this.socket.onerror=null,this.socket.onclose=null,this.socket=null,this.destination=null,this.options=null,this.url=null},t.prototype.start=function(){this.shouldAttemptReconnect=!!this.reconnectInterval,this.progress=0,this.established=!1,this.socket=new WebSocket(this.url,this.options.protocols||null),this.socket.binaryType="arraybuffer",this.socket.onmessage=this.onMessage.bind(this),this.socket.onopen=this.onOpen.bind(this),this.socket.onerror=this.onClose.bind(this),this.socket.onclose=this.onClose.bind(this)},t.prototype.resume=function(t){},t.prototype.onOpen=function(){this.progress=1,this.established=!0},t.prototype.onClose=function(){this.shouldAttemptReconnect&&(clearTimeout(this.reconnectTimeoutId),this.reconnectTimeoutId=setTimeout(function(){this.start()}.bind(this),1e3*this.reconnectInterval))},t.prototype.onMessage=function(t){this.destination&&this.destination.write(t.data)},t}(),F.Demuxer.TS=function(){var t=function(t){this.bits=null,this.leftoverBytes=null,this.guessVideoFrameEnd=!0,this.pidsToStreamIds={},this.pesPacketInfo={},this.startTime=0,this.currentTime=0,Object.defineProperty(this,"videoBytes",{get:this.getVideoBytes}),Object.defineProperty(this,"videoFrameCnt",{get:this.getVideoFrameCnt}),Object.defineProperty(this,"audioBytes",{get:this.getAudioBytes}),Object.defineProperty(this,"audioFrameCnt",{get:this.getAudioFrameCnt})};return t.prototype.connect=function(t,e){this.pesPacketInfo[t]={destination:e,currentLength:0,totalLength:0,pts:0,buffers:[],bytes:0,frameCnt:0}},t.prototype.destroy=function(){var e=this.pesPacketInfo[t.STREAM.VIDEO_1];void 0!==e.destination&&(e.destination=null),void 0!==(e=this.pesPacketInfo[t.STREAM.AUDIO_1]).destination&&(e.destination=null)},t.prototype.write=function(t){if(this.leftoverBytes){var e=t.byteLength+this.leftoverBytes.byteLength;this.bits=new F.BitBuffer(e),this.bits.write([this.leftoverBytes,t])}else this.bits=new F.BitBuffer(t);for(;this.bits.has(1504)&&this.parsePacket(););var i=this.bits.byteLength-(this.bits.index>>3);this.leftoverBytes=i>0?this.bits.bytes.subarray(this.bits.index>>3):null},t.prototype.parsePacket=function(){if(71!==this.bits.read(8)&&!this.resync())return!1;var t=187+(this.bits.index>>3),e=(this.bits.read(1),this.bits.read(1)),i=(this.bits.read(1),this.bits.read(13)),r=(this.bits.read(2),this.bits.read(2)),s=(this.bits.read(4),this.pidsToStreamIds[i]);if(e&&s){(f=this.pesPacketInfo[s])&&f.currentLength&&this.packetComplete(f)}if(1&r){if(2&r){var o=this.bits.read(8);this.bits.skip(o<<3)}if(e&&this.bits.nextBytesAreStartCode()){this.bits.skip(24),s=this.bits.read(8),this.pidsToStreamIds[i]=s;var n=this.bits.read(16);this.bits.skip(8);var a=this.bits.read(2);this.bits.skip(6);var h=this.bits.read(8),l=this.bits.index+(h<<3);if(f=this.pesPacketInfo[s]){var d=0;if(2&a){this.bits.skip(4);var u=this.bits.read(3);this.bits.skip(1);var c=this.bits.read(15);this.bits.skip(1);var p=this.bits.read(15);this.bits.skip(1),d=(1073741824*u+32768*c+p)/9e4,this.currentTime=d,-1===this.startTime&&(this.startTime=d)}var g=n?n-h-3:0;this.packetStart(f,d,g)}this.bits.index=l}if(s){var f=this.pesPacketInfo[s];if(f){var m=this.bits.index>>3,b=this.packetAddData(f,m,t),y=!e&&2&r;(b||this.guessVideoFrameEnd&&y)&&this.packetComplete(f)}}}return this.bits.index=t<<3,!0},t.prototype.resync=function(){if(!this.bits.has(9024))return!1;for(var t=this.bits.index>>3,e=0;e<187;e++)if(71===this.bits.bytes[t+e]){for(var i=!0,r=1;r<5;r++)if(71!==this.bits.bytes[t+e+188*r]){i=!1;break}if(i)return this.bits.index=t+e+1<<3,!0}return console.warn("JSMpeg: Possible garbage data. Skipping."),this.bits.skip(1496),!1},t.prototype.packetStart=function(t,e,i){t.totalLength=i,t.currentLength=0,t.pts=e},t.prototype.packetAddData=function(t,e,i){t.buffers.push(this.bits.bytes.subarray(e,i)),t.currentLength+=i-e;return 0!==t.totalLength&&t.currentLength>=t.totalLength},t.prototype.packetComplete=function(t){t.destination.write(t.pts,t.buffers),t.bytes=t.bytes+t.currentLength,t.frameCnt++,t.totalLength=0,t.currentLength=0,t.buffers=[]},t.prototype.getAudioBytes=function(){return this.pesPacketInfo[t.STREAM.AUDIO_1].bytes},t.prototype.getAudioFrameCnt=function(){return this.pesPacketInfo[t.STREAM.AUDIO_1].frameCnt},t.prototype.getVideoBytes=function(){return this.pesPacketInfo[t.STREAM.VIDEO_1].bytes},t.prototype.getVideoFrameCnt=function(){return this.pesPacketInfo[t.STREAM.VIDEO_1].frameCnt},t.STREAM={PACK_HEADER:186,SYSTEM_HEADER:187,PROGRAM_MAP:188,PRIVATE_1:189,PADDING:190,PRIVATE_2:191,AUDIO_1:192,VIDEO_1:224,DIRECTORY:255},t}(),F.Decoder.Base=function(){var t=function(t){this.destination=null,this.canPlay=!1,this.collectTimestamps=!t.streaming,this.timestamps=[],this.timestampIndex=0,this.startTime=0,this.decodedTime=0,this.decodedFrameCnt=0,this.decoderResetCnt=0,Object.defineProperty(this,"currentTime",{get:this.getCurrentTime}),Object.defineProperty(this,"frameCnt",{get:this.getDecodedFrameCnt})};return t.prototype.connect=function(t){this.destination=t},t.prototype.destroy=function(){this.destination=null},t.prototype.write=function(t,e){this.collectTimestamps&&(0===this.timestamps.length&&(this.startTime=t,this.decodedTime=t),this.timestamps.push({index:this.bits.byteLength<<3,time:t})),this.bits.write(e),this.canPlay=!0},t.prototype.seek=function(t){if(this.collectTimestamps){this.timestampIndex=0;for(var e=0;e<this.timestamps.length&&!(this.timestamps[e].time>t);e++)this.timestampIndex=e;var i=this.timestamps[this.timestampIndex];i?(this.bits.index=i.index,this.decodedTime=i.time):(this.bits.index=0,this.decodedTime=this.startTime)}},t.prototype.decode=function(){this.advanceDecodedTime(0)},t.prototype.advanceDecodedTime=function(t){if(this.collectTimestamps){for(var e=-1,i=this.timestampIndex;i<this.timestamps.length&&!(this.timestamps[i].index>this.bits.index);i++)e=i;if(-1!==e&&e!==this.timestampIndex)return this.timestampIndex=e,void(this.decodedTime=this.timestamps[this.timestampIndex].time)}this.decodedTime+=t},t.prototype.getCurrentTime=function(){return this.decodedTime},t.prototype.getDecodedFrameCnt=function(){return this.decodedFrameCnt},t}(),F.Decoder.MPEG1Video=function(){var t=function(t){F.Decoder.Base.call(this,t);var e=t.videoBufferSize||524288,i=t.streaming?F.BitBuffer.MODE.EVICT:F.BitBuffer.MODE.EXPAND;this.bits=new F.BitBuffer(e,i),this.customIntraQuantMatrix=new Uint8Array(64),this.customNonIntraQuantMatrix=new Uint8Array(64),this.blockData=new Int32Array(64),this.currentFrame=0,this.decodeFirstFrame=!1!==t.decodeFirstFrame,this._resetCnt=0,Object.defineProperty(this,"resetCnt",{get:this.getResetCnt}),this.enabled=!0};return t.prototype=Object.create(F.Decoder.Base.prototype),t.prototype.constructor=t,t.prototype.write=function(e,i){if(this.enabled&&(F.Decoder.Base.prototype.write.call(this,e,i),!this.hasSequenceHeader)){if(-1===this.bits.findStartCode(t.START.SEQUENCE))return!1;this.decodeSequenceHeader(),this.decodeFirstFrame&&this.decode()}},t.prototype.decode=function(){if(!this.hasSequenceHeader)return!1;if(-1===this.bits.findStartCode(t.START.PICTURE)){this.bits.byteLength,this.bits.index;return!1}return this.decodePicture(),this.advanceDecodedTime(1/this.frameRate),!0},t.prototype.readHuffman=function(t){var e=0;do{e=t[e+this.bits.read(1)]}while(e>=0&&0!==t[e]);return t[e+2]},t.prototype.getResetCnt=function(){return this._resetCnt},t.prototype.setEnable=function(t){this.enabled=t,t||(this.hasSequenceHeader=!1)},t.prototype.frameRate=30,t.prototype.decodeSequenceHeader=function(){var e=this.bits.read(12),i=this.bits.read(12);if(this.bits.skip(4),this.frameRate=t.PICTURE_RATE[this.bits.read(4)],this.bits.skip(30),e===this.width&&i===this.height||(this.width=e,this.height=i,this.initBuffers(),this.destination&&(this.resizeCnt++,this.destination.resize(e,i))),this.bits.read(1)){for(r=0;r<64;r++)this.customIntraQuantMatrix[t.ZIG_ZAG[r]]=this.bits.read(8);this.intraQuantMatrix=this.customIntraQuantMatrix}if(this.bits.read(1)){for(var r=0;r<64;r++){var s=t.ZIG_ZAG[r];this.customNonIntraQuantMatrix[s]=this.bits.read(8)}this.nonIntraQuantMatrix=this.customNonIntraQuantMatrix}this.hasSequenceHeader=!0},t.prototype.initBuffers=function(){this.intraQuantMatrix=t.DEFAULT_INTRA_QUANT_MATRIX,this.nonIntraQuantMatrix=t.DEFAULT_NON_INTRA_QUANT_MATRIX,this.mbWidth=this.width+15>>4,this.mbHeight=this.height+15>>4,this.mbSize=this.mbWidth*this.mbHeight,this.codedWidth=this.mbWidth<<4,this.codedHeight=this.mbHeight<<4,this.codedSize=this.codedWidth*this.codedHeight,this.halfWidth=this.mbWidth<<3,this.halfHeight=this.mbHeight<<3,this.currentY=new Uint8ClampedArray(this.codedSize),this.currentY32=new Uint32Array(this.currentY.buffer),this.currentCr=new Uint8ClampedArray(this.codedSize>>2),this.currentCr32=new Uint32Array(this.currentCr.buffer),this.currentCb=new Uint8ClampedArray(this.codedSize>>2),this.currentCb32=new Uint32Array(this.currentCb.buffer),this.forwardY=new Uint8ClampedArray(this.codedSize),this.forwardY32=new Uint32Array(this.forwardY.buffer),this.forwardCr=new Uint8ClampedArray(this.codedSize>>2),this.forwardCr32=new Uint32Array(this.forwardCr.buffer),this.forwardCb=new Uint8ClampedArray(this.codedSize>>2),this.forwardCb32=new Uint32Array(this.forwardCb.buffer)},t.prototype.currentY=null,t.prototype.currentCr=null,t.prototype.currentCb=null,t.prototype.pictureType=0,t.prototype.forwardY=null,t.prototype.forwardCr=null,t.prototype.forwardCb=null,t.prototype.fullPelForward=!1,t.prototype.forwardFCode=0,t.prototype.forwardRSize=0,t.prototype.forwardF=0,t.prototype.decodePicture=function(e){if(this.currentFrame++,this.bits.skip(10),this.pictureType=this.bits.read(3),this.bits.skip(16),!(this.pictureType<=0||this.pictureType>=t.PICTURE_TYPE.B)){if(this.pictureType===t.PICTURE_TYPE.PREDICTIVE){if(this.fullPelForward=this.bits.read(1),this.forwardFCode=this.bits.read(3),0===this.forwardFCode)return;this.forwardRSize=this.forwardFCode-1,this.forwardF=1<<this.forwardRSize}var i=0;do{i=this.bits.findNextStartCode()}while(i===t.START.EXTENSION||i===t.START.USER_DATA);for(;i>=t.START.SLICE_FIRST&&i<=t.START.SLICE_LAST;)this.decodeSlice(255&i),i=this.bits.findNextStartCode();if(-1!==i&&this.bits.rewind(32),this.decodedFrameCnt++,this.destination&&this.destination.render(this.currentY,this.currentCr,this.currentCb),this.pictureType===t.PICTURE_TYPE.INTRA||this.pictureType===t.PICTURE_TYPE.PREDICTIVE){var r=this.forwardY,s=this.forwardY32,o=this.forwardCr,n=this.forwardCr32,a=this.forwardCb,h=this.forwardCb32;this.forwardY=this.currentY,this.forwardY32=this.currentY32,this.forwardCr=this.currentCr,this.forwardCr32=this.currentCr32,this.forwardCb=this.currentCb,this.forwardCb32=this.currentCb32,this.currentY=r,this.currentY32=s,this.currentCr=o,this.currentCr32=n,this.currentCb=a,this.currentCb32=h}}},t.prototype.quantizerScale=0,t.prototype.sliceBegin=!1,t.prototype.decodeSlice=function(t){for(this.sliceBegin=!0,this.macroblockAddress=(t-1)*this.mbWidth-1,this.motionFwH=this.motionFwHPrev=0,this.motionFwV=this.motionFwVPrev=0,this.dcPredictorY=128,this.dcPredictorCr=128,this.dcPredictorCb=128,this.quantizerScale=this.bits.read(5);this.bits.read(1);)this.bits.skip(8);do{this.decodeMacroblock()}while(!this.bits.nextBytesAreStartCode())},t.prototype.macroblockAddress=0,t.prototype.mbRow=0,t.prototype.mbCol=0,t.prototype.macroblockType=0,t.prototype.macroblockIntra=!1,t.prototype.macroblockMotFw=!1,t.prototype.motionFwH=0,t.prototype.motionFwV=0,t.prototype.motionFwHPrev=0,t.prototype.motionFwVPrev=0,t.prototype.decodeMacroblock=function(){for(var e=0,i=this.readHuffman(t.MACROBLOCK_ADDRESS_INCREMENT);34===i;)i=this.readHuffman(t.MACROBLOCK_ADDRESS_INCREMENT);for(;35===i;)e+=33,i=this.readHuffman(t.MACROBLOCK_ADDRESS_INCREMENT);if(e+=i,this.sliceBegin)this.sliceBegin=!1,this.macroblockAddress+=e;else{if(this.macroblockAddress+e>=this.mbSize)return;for(e>1&&(this.dcPredictorY=128,this.dcPredictorCr=128,this.dcPredictorCb=128,this.pictureType===t.PICTURE_TYPE.PREDICTIVE&&(this.motionFwH=this.motionFwHPrev=0,this.motionFwV=this.motionFwVPrev=0));e>1;)this.macroblockAddress++,this.mbRow=this.macroblockAddress/this.mbWidth|0,this.mbCol=this.macroblockAddress%this.mbWidth,this.copyMacroblock(this.motionFwH,this.motionFwV,this.forwardY,this.forwardCr,this.forwardCb),e--;this.macroblockAddress++}this.mbRow=this.macroblockAddress/this.mbWidth|0,this.mbCol=this.macroblockAddress%this.mbWidth;var r=t.MACROBLOCK_TYPE[this.pictureType];this.macroblockType=this.readHuffman(r),this.macroblockIntra=1&this.macroblockType,this.macroblockMotFw=8&this.macroblockType,0!=(16&this.macroblockType)&&(this.quantizerScale=this.bits.read(5)),this.macroblockIntra?(this.motionFwH=this.motionFwHPrev=0,this.motionFwV=this.motionFwVPrev=0):(this.dcPredictorY=128,this.dcPredictorCr=128,this.dcPredictorCb=128,this.decodeMotionVectors(),this.copyMacroblock(this.motionFwH,this.motionFwV,this.forwardY,this.forwardCr,this.forwardCb));for(var s=0!=(2&this.macroblockType)?this.readHuffman(t.CODE_BLOCK_PATTERN):this.macroblockIntra?63:0,o=0,n=32;o<6;o++)0!=(s&n)&&this.decodeBlock(o),n>>=1},t.prototype.decodeMotionVectors=function(){var e,i,r=0;this.macroblockMotFw?(0!==(e=this.readHuffman(t.MOTION))&&1!==this.forwardF?(r=this.bits.read(this.forwardRSize),i=(Math.abs(e)-1<<this.forwardRSize)+r+1,e<0&&(i=-i)):i=e,this.motionFwHPrev+=i,this.motionFwHPrev>(this.forwardF<<4)-1?this.motionFwHPrev-=this.forwardF<<5:this.motionFwHPrev<-this.forwardF<<4&&(this.motionFwHPrev+=this.forwardF<<5),this.motionFwH=this.motionFwHPrev,this.fullPelForward&&(this.motionFwH<<=1),0!==(e=this.readHuffman(t.MOTION))&&1!==this.forwardF?(r=this.bits.read(this.forwardRSize),i=(Math.abs(e)-1<<this.forwardRSize)+r+1,e<0&&(i=-i)):i=e,this.motionFwVPrev+=i,this.motionFwVPrev>(this.forwardF<<4)-1?this.motionFwVPrev-=this.forwardF<<5:this.motionFwVPrev<-this.forwardF<<4&&(this.motionFwVPrev+=this.forwardF<<5),this.motionFwV=this.motionFwVPrev,this.fullPelForward&&(this.motionFwV<<=1)):this.pictureType===t.PICTURE_TYPE.PREDICTIVE&&(this.motionFwH=this.motionFwHPrev=0,this.motionFwV=this.motionFwVPrev=0)},t.prototype.copyMacroblock=function(t,e,i,r,s){var o,n,a,h,l,d,u,c,p,g=this.currentY32,f=this.currentCb32,m=this.currentCr32;n=(o=this.codedWidth)-16,a=t>>1,h=e>>1,l=1==(1&t),d=1==(1&e),u=((this.mbRow<<4)+h)*o+(this.mbCol<<4)+a,p=(c=this.mbRow*o+this.mbCol<<2)+(o<<2);var b,y,v,T;if(l)if(d)for(;c<p;){for(y=i[u]+i[u+o],u++,b=0;b<4;b++)T=y+(v=i[u]+i[u+o])+2>>2&255,T|=(y=i[++u]+i[u+o])+v+2<<6&65280,T|=y+(v=i[++u]+i[u+o])+2<<14&16711680,y=i[++u]+i[u+o],u++,T|=y+v+2<<22&4278190080,g[c++]=T;c+=n>>2,u+=n-1}else for(;c<p;){for(y=i[u++],b=0;b<4;b++)T=y+(v=i[u++])+1>>1&255,T|=(y=i[u++])+v+1<<7&65280,T|=y+(v=i[u++])+1<<15&16711680,T|=(y=i[u++])+v+1<<23&4278190080,g[c++]=T;c+=n>>2,u+=n-1}else if(d)for(;c<p;){for(b=0;b<4;b++)T=i[u]+i[u+o]+1>>1&255,T|=i[++u]+i[u+o]+1<<7&65280,T|=i[++u]+i[u+o]+1<<15&16711680,T|=i[++u]+i[u+o]+1<<23&4278190080,u++,g[c++]=T;c+=n>>2,u+=n}else for(;c<p;){for(b=0;b<4;b++)T=i[u],T|=i[++u]<<8,T|=i[++u]<<16,T|=i[++u]<<24,u++,g[c++]=T;c+=n>>2,u+=n}n=(o=this.halfWidth)-8,a=t/2>>1,h=e/2>>1,l=1==(t/2&1),d=1==(e/2&1),u=((this.mbRow<<3)+h)*o+(this.mbCol<<3)+a,p=(c=this.mbRow*o+this.mbCol<<1)+(o<<1);var w,C,_,E,x,R;if(l)if(d)for(;c<p;){for(w=r[u]+r[u+o],E=s[u]+s[u+o],u++,b=0;b<2;b++)_=w+(C=r[u]+r[u+o])+2>>2&255,R=E+(x=s[u]+s[u+o])+2>>2&255,_|=(w=r[++u]+r[u+o])+C+2<<6&65280,R|=(E=s[u]+s[u+o])+x+2<<6&65280,_|=w+(C=r[++u]+r[u+o])+2<<14&16711680,R|=E+(x=s[u]+s[u+o])+2<<14&16711680,w=r[++u]+r[u+o],E=s[u]+s[u+o],u++,_|=w+C+2<<22&4278190080,R|=E+x+2<<22&4278190080,m[c]=_,f[c]=R,c++;c+=n>>2,u+=n-1}else for(;c<p;){for(w=r[u],E=s[u],u++,b=0;b<2;b++)_=w+(C=r[u])+1>>1&255,R=E+(x=s[u++])+1>>1&255,_|=(w=r[u])+C+1<<7&65280,R|=(E=s[u++])+x+1<<7&65280,_|=w+(C=r[u])+1<<15&16711680,R|=E+(x=s[u++])+1<<15&16711680,_|=(w=r[u])+C+1<<23&4278190080,R|=(E=s[u++])+x+1<<23&4278190080,m[c]=_,f[c]=R,c++;c+=n>>2,u+=n-1}else if(d)for(;c<p;){for(b=0;b<2;b++)_=r[u]+r[u+o]+1>>1&255,R=s[u]+s[u+o]+1>>1&255,_|=r[++u]+r[u+o]+1<<7&65280,R|=s[u]+s[u+o]+1<<7&65280,_|=r[++u]+r[u+o]+1<<15&16711680,R|=s[u]+s[u+o]+1<<15&16711680,_|=r[++u]+r[u+o]+1<<23&4278190080,R|=s[u]+s[u+o]+1<<23&4278190080,u++,m[c]=_,f[c]=R,c++;c+=n>>2,u+=n}else for(;c<p;){for(b=0;b<2;b++)_=r[u],R=s[u],_|=r[++u]<<8,R|=s[u]<<8,_|=r[++u]<<16,R|=s[u]<<16,_|=r[++u]<<24,R|=s[u]<<24,u++,m[c]=_,f[c]=R,c++;c+=n>>2,u+=n}},t.prototype.dcPredictorY=0,t.prototype.dcPredictorCr=0,t.prototype.dcPredictorCb=0,t.prototype.blockData=null,t.prototype.decodeBlock=function(e){var i,r=0;if(this.macroblockIntra){var s,o;if(e<4?(s=this.dcPredictorY,o=this.readHuffman(t.DCT_DC_SIZE_LUMINANCE)):(s=4===e?this.dcPredictorCr:this.dcPredictorCb,o=this.readHuffman(t.DCT_DC_SIZE_CHROMINANCE)),o>0){var n=this.bits.read(o);this.blockData[0]=0!=(n&1<<o-1)?s+n:s+(-1<<o|n+1)}else this.blockData[0]=s;e<4?this.dcPredictorY=this.blockData[0]:4===e?this.dcPredictorCr=this.blockData[0]:this.dcPredictorCb=this.blockData[0],this.blockData[0]<<=8,i=this.intraQuantMatrix,r=1}else i=this.nonIntraQuantMatrix;for(var a=0;;){var h=0,l=this.readHuffman(t.DCT_COEFF);if(1===l&&r>0&&0===this.bits.read(1))break;65535===l?(h=this.bits.read(6),0===(a=this.bits.read(8))?a=this.bits.read(8):128===a?a=this.bits.read(8)-256:a>128&&(a-=256)):(h=l>>8,a=255&l,this.bits.read(1)&&(a=-a));var d=t.ZIG_ZAG[r+=h];r++,a<<=1,this.macroblockIntra||(a+=a<0?-1:1),0==(1&(a=a*this.quantizerScale*i[d]>>4))&&(a-=a>0?1:-1),a>2047?a=2047:a<-2048&&(a=-2048),this.blockData[d]=a*t.PREMULTIPLIER_MATRIX[d]}var u,c,p;e<4?(u=this.currentY,p=this.codedWidth-8,c=this.mbRow*this.codedWidth+this.mbCol<<4,0!=(1&e)&&(c+=8),0!=(2&e)&&(c+=this.codedWidth<<3)):(u=4===e?this.currentCb:this.currentCr,p=(this.codedWidth>>1)-8,c=(this.mbRow*this.codedWidth<<2)+(this.mbCol<<3)),this.macroblockIntra?1===r?(t.CopyValueToDestination(this.blockData[0]+128>>8,u,c,p),this.blockData[0]=0):(t.IDCT(this.blockData),t.CopyBlockToDestination(this.blockData,u,c,p),F.Fill(this.blockData,0)):1===r?(t.AddValueToDestination(this.blockData[0]+128>>8,u,c,p),this.blockData[0]=0):(t.IDCT(this.blockData),t.AddBlockToDestination(this.blockData,u,c,p),F.Fill(this.blockData,0)),r=0},t.CopyBlockToDestination=function(t,e,i,r){for(var s=0;s<64;s+=8,i+=r+8)e[i+0]=t[s+0],e[i+1]=t[s+1],e[i+2]=t[s+2],e[i+3]=t[s+3],e[i+4]=t[s+4],e[i+5]=t[s+5],e[i+6]=t[s+6],e[i+7]=t[s+7]},t.AddBlockToDestination=function(t,e,i,r){for(var s=0;s<64;s+=8,i+=r+8)e[i+0]+=t[s+0],e[i+1]+=t[s+1],e[i+2]+=t[s+2],e[i+3]+=t[s+3],e[i+4]+=t[s+4],e[i+5]+=t[s+5],e[i+6]+=t[s+6],e[i+7]+=t[s+7]},t.CopyValueToDestination=function(t,e,i,r){for(var s=0;s<64;s+=8,i+=r+8)e[i+0]=t,e[i+1]=t,e[i+2]=t,e[i+3]=t,e[i+4]=t,e[i+5]=t,e[i+6]=t,e[i+7]=t},t.AddValueToDestination=function(t,e,i,r){for(var s=0;s<64;s+=8,i+=r+8)e[i+0]+=t,e[i+1]+=t,e[i+2]+=t,e[i+3]+=t,e[i+4]+=t,e[i+5]+=t,e[i+6]+=t,e[i+7]+=t},t.IDCT=function(t){for(var e,i,r,s,o,n,a,h,l,d,u,c,p,g,f,m,b,y,v=0;v<8;++v)e=t[32+v],i=t[16+v]+t[48+v],r=t[40+v]-t[24+v],n=t[8+v]+t[56+v],a=t[24+v]+t[40+v],l=(p=(473*(s=t[8+v]-t[56+v])-196*r+128>>8)-(o=n+a))-(362*(n-a)+128>>8),g=(d=(h=t[0+v])-e)+(u=(362*(t[16+v]-t[48+v])+128>>8)-i),f=(c=h+e)+i,m=d-u,b=c-i,y=-l-(473*r+196*s+128>>8),t[0+v]=o+f,t[8+v]=p+g,t[16+v]=m-l,t[24+v]=b-y,t[32+v]=b+y,t[40+v]=l+m,t[48+v]=g-p,t[56+v]=f-o;for(v=0;v<64;v+=8)e=t[4+v],i=t[2+v]+t[6+v],r=t[5+v]-t[3+v],n=t[1+v]+t[7+v],a=t[3+v]+t[5+v],l=(p=(473*(s=t[1+v]-t[7+v])-196*r+128>>8)-(o=n+a))-(362*(n-a)+128>>8),g=(d=(h=t[0+v])-e)+(u=(362*(t[2+v]-t[6+v])+128>>8)-i),f=(c=h+e)+i,m=d-u,b=c-i,y=-l-(473*r+196*s+128>>8),t[0+v]=o+f+128>>8,t[1+v]=p+g+128>>8,t[2+v]=m-l+128>>8,t[3+v]=b-y+128>>8,t[4+v]=b+y+128>>8,t[5+v]=l+m+128>>8,t[6+v]=g-p+128>>8,t[7+v]=f-o+128>>8},t.PICTURE_RATE=[0,23.976,24,25,29.97,30,50,59.94,60,0,0,0,0,0,0,0],t.ZIG_ZAG=new Uint8Array([0,1,8,16,9,2,3,10,17,24,32,25,18,11,4,5,12,19,26,33,40,48,41,34,27,20,13,6,7,14,21,28,35,42,49,56,57,50,43,36,29,22,15,23,30,37,44,51,58,59,52,45,38,31,39,46,53,60,61,54,47,55,62,63]),t.DEFAULT_INTRA_QUANT_MATRIX=new Uint8Array([8,16,19,22,26,27,29,34,16,16,22,24,27,29,34,37,19,22,26,27,29,34,34,38,22,22,26,27,29,34,37,40,22,26,27,29,32,35,40,48,26,27,29,32,35,40,48,58,26,27,29,34,38,46,56,69,27,29,35,38,46,56,69,83]),t.DEFAULT_NON_INTRA_QUANT_MATRIX=new Uint8Array([16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16]),t.PREMULTIPLIER_MATRIX=new Uint8Array([32,44,42,38,32,25,17,9,44,62,58,52,44,35,24,12,42,58,55,49,42,33,23,12,38,52,49,44,38,30,20,10,32,44,42,38,32,25,17,9,25,35,33,30,25,20,14,7,17,24,23,20,17,14,9,5,9,12,12,10,9,7,5,2]),t.MACROBLOCK_ADDRESS_INCREMENT=new Int16Array([3,6,0,9,12,0,0,0,1,15,18,0,21,24,0,27,30,0,33,36,0,0,0,3,0,0,2,39,42,0,45,48,0,0,0,5,0,0,4,51,54,0,57,60,0,0,0,7,0,0,6,63,66,0,69,72,0,75,78,0,81,84,0,-1,87,0,-1,90,0,93,96,0,99,102,0,105,108,0,111,114,0,0,0,9,0,0,8,117,120,0,123,126,0,129,132,0,135,138,0,0,0,15,0,0,14,0,0,13,0,0,12,0,0,11,0,0,10,141,-1,0,-1,144,0,147,150,0,153,156,0,159,162,0,165,168,0,171,174,0,177,180,0,183,-1,0,-1,186,0,189,192,0,195,198,0,201,204,0,207,210,0,213,216,0,219,222,0,0,0,21,0,0,20,0,0,19,0,0,18,0,0,17,0,0,16,0,0,35,0,0,34,0,0,33,0,0,32,0,0,31,0,0,30,0,0,29,0,0,28,0,0,27,0,0,26,0,0,25,0,0,24,0,0,23,0,0,22]),t.MACROBLOCK_TYPE_INTRA=new Int8Array([3,6,0,-1,9,0,0,0,1,0,0,17]),t.MACROBLOCK_TYPE_PREDICTIVE=new Int8Array([3,6,0,9,12,0,0,0,10,15,18,0,0,0,2,21,24,0,0,0,8,27,30,0,33,36,0,-1,39,0,0,0,18,0,0,26,0,0,1,0,0,17]),t.MACROBLOCK_TYPE_B=new Int8Array([3,6,0,9,15,0,12,18,0,24,21,0,0,0,12,27,30,0,0,0,14,39,42,0,36,33,0,0,0,4,0,0,6,54,48,0,45,51,0,0,0,8,0,0,10,-1,57,0,0,0,1,60,63,0,0,0,30,0,0,17,0,0,22,0,0,26]),t.MACROBLOCK_TYPE=[null,t.MACROBLOCK_TYPE_INTRA,t.MACROBLOCK_TYPE_PREDICTIVE,t.MACROBLOCK_TYPE_B],t.CODE_BLOCK_PATTERN=new Int16Array([6,3,0,9,18,0,12,15,0,24,33,0,36,39,0,27,21,0,30,42,0,60,57,0,54,48,0,69,51,0,81,75,0,63,84,0,45,66,0,72,78,0,0,0,60,105,120,0,132,144,0,114,108,0,126,141,0,87,93,0,117,96,0,0,0,32,135,138,0,99,123,0,129,102,0,0,0,4,90,111,0,0,0,8,0,0,16,0,0,44,150,168,0,0,0,28,0,0,52,0,0,62,183,177,0,156,180,0,0,0,1,165,162,0,0,0,61,0,0,56,171,174,0,0,0,2,0,0,40,153,186,0,0,0,48,192,189,0,147,159,0,0,0,20,0,0,12,240,249,0,0,0,63,231,225,0,195,219,0,252,198,0,0,0,24,0,0,36,0,0,3,207,261,0,243,237,0,204,213,0,210,234,0,201,228,0,216,222,0,258,255,0,264,246,0,-1,282,0,285,291,0,0,0,33,0,0,9,318,330,0,306,348,0,0,0,5,0,0,10,279,267,0,0,0,6,0,0,18,0,0,17,0,0,34,339,357,0,309,312,0,270,276,0,327,321,0,351,354,0,303,297,0,294,288,0,300,273,0,342,345,0,315,324,0,336,333,0,363,375,0,0,0,41,0,0,14,0,0,21,372,366,0,360,369,0,0,0,11,0,0,19,0,0,7,0,0,35,0,0,13,0,0,50,0,0,49,0,0,58,0,0,37,0,0,25,0,0,45,0,0,57,0,0,26,0,0,29,0,0,38,0,0,53,0,0,23,0,0,43,0,0,46,0,0,42,0,0,22,0,0,54,0,0,51,0,0,15,0,0,30,0,0,39,0,0,47,0,0,55,0,0,27,0,0,59,0,0,31]),t.MOTION=new Int16Array([3,6,0,12,9,0,0,0,0,18,15,0,24,21,0,0,0,-1,0,0,1,27,30,0,36,33,0,0,0,2,0,0,-2,42,45,0,48,39,0,60,54,0,0,0,3,0,0,-3,51,57,0,-1,69,0,81,75,0,78,63,0,72,66,0,96,84,0,87,93,0,-1,99,0,108,105,0,0,0,-4,90,102,0,0,0,4,0,0,-7,0,0,5,111,123,0,0,0,-5,0,0,7,114,120,0,126,117,0,0,0,-6,0,0,6,153,162,0,150,147,0,135,138,0,156,141,0,129,159,0,132,144,0,0,0,10,0,0,9,0,0,8,0,0,-8,171,198,0,0,0,-9,180,192,0,168,183,0,165,186,0,174,189,0,0,0,-10,177,195,0,0,0,12,0,0,16,0,0,13,0,0,14,0,0,11,0,0,15,0,0,-16,0,0,-12,0,0,-14,0,0,-15,0,0,-11,0,0,-13]),t.DCT_DC_SIZE_LUMINANCE=new Int8Array([6,3,0,18,15,0,9,12,0,0,0,1,0,0,2,27,24,0,21,30,0,0,0,0,36,33,0,0,0,4,0,0,3,39,42,0,0,0,5,0,0,6,48,45,0,51,-1,0,0,0,7,0,0,8]),t.DCT_DC_SIZE_CHROMINANCE=new Int8Array([6,3,0,12,9,0,18,15,0,24,21,0,0,0,2,0,0,1,0,0,0,30,27,0,0,0,3,36,33,0,0,0,4,42,39,0,0,0,5,48,45,0,0,0,6,51,-1,0,0,0,7,0,0,8]),t.DCT_COEFF=new Int32Array([3,6,0,12,9,0,0,0,1,21,24,0,18,15,0,39,27,0,33,30,0,42,36,0,0,0,257,60,66,0,54,63,0,48,57,0,0,0,513,51,45,0,0,0,2,0,0,3,81,75,0,87,93,0,72,78,0,96,90,0,0,0,1025,69,84,0,0,0,769,0,0,258,0,0,1793,0,0,65535,0,0,1537,111,108,0,0,0,1281,105,102,0,117,114,0,99,126,0,120,123,0,156,150,0,162,159,0,144,147,0,129,135,0,138,132,0,0,0,2049,0,0,4,0,0,514,0,0,2305,153,141,0,165,171,0,180,168,0,177,174,0,183,186,0,0,0,2561,0,0,3329,0,0,6,0,0,259,0,0,5,0,0,770,0,0,2817,0,0,3073,228,225,0,201,210,0,219,213,0,234,222,0,216,231,0,207,192,0,204,189,0,198,195,0,243,261,0,273,240,0,246,237,0,249,258,0,279,276,0,252,255,0,270,282,0,264,267,0,0,0,515,0,0,260,0,0,7,0,0,1026,0,0,1282,0,0,4097,0,0,3841,0,0,3585,315,321,0,333,342,0,312,291,0,375,357,0,288,294,0,-1,369,0,285,303,0,318,363,0,297,306,0,339,309,0,336,348,0,330,300,0,372,345,0,351,366,0,327,354,0,360,324,0,381,408,0,417,420,0,390,378,0,435,438,0,384,387,0,0,0,2050,396,402,0,465,462,0,0,0,8,411,399,0,429,432,0,453,414,0,426,423,0,0,0,10,0,0,9,0,0,11,0,0,5377,0,0,1538,0,0,771,0,0,5121,0,0,1794,0,0,4353,0,0,4609,0,0,4865,444,456,0,0,0,1027,459,450,0,0,0,261,393,405,0,0,0,516,447,441,0,516,519,0,486,474,0,510,483,0,504,498,0,471,537,0,507,501,0,522,513,0,534,531,0,468,477,0,492,495,0,549,546,0,525,528,0,0,0,263,0,0,2562,0,0,2306,0,0,5633,0,0,5889,0,0,6401,0,0,6145,0,0,1283,0,0,772,0,0,13,0,0,12,0,0,14,0,0,15,0,0,517,0,0,6657,0,0,262,540,543,0,480,489,0,588,597,0,0,0,27,609,555,0,606,603,0,0,0,19,0,0,22,591,621,0,0,0,18,573,576,0,564,570,0,0,0,20,552,582,0,0,0,21,558,579,0,0,0,23,612,594,0,0,0,25,0,0,24,600,615,0,0,0,31,0,0,30,0,0,28,0,0,29,0,0,26,0,0,17,0,0,16,567,618,0,561,585,0,654,633,0,0,0,37,645,648,0,0,0,36,630,636,0,0,0,34,639,627,0,663,666,0,657,624,0,651,642,0,669,660,0,0,0,35,0,0,267,0,0,40,0,0,268,0,0,266,0,0,32,0,0,264,0,0,265,0,0,38,0,0,269,0,0,270,0,0,33,0,0,39,0,0,7937,0,0,6913,0,0,7681,0,0,4098,0,0,7425,0,0,7169,0,0,271,0,0,274,0,0,273,0,0,272,0,0,1539,0,0,2818,0,0,3586,0,0,3330,0,0,3074,0,0,3842]),t.PICTURE_TYPE={INTRA:1,PREDICTIVE:2,B:3},t.START={SEQUENCE:179,SLICE_FIRST:1,SLICE_LAST:175,PICTURE:0,EXTENSION:181,USER_DATA:178},t}(),F.Decoder.MP2Audio=function(){var t=function(e){F.Decoder.Base.call(this,e);var i=e.audioBufferSize||131072,r=e.streaming?F.BitBuffer.MODE.EVICT:F.BitBuffer.MODE.EXPAND;this.bits=new F.BitBuffer(i,r),this.left=new Float32Array(1152),this.right=new Float32Array(1152),this.sampleRate=44100,this.D=new Float32Array(1024),this.D.set(t.SYNTHESIS_WINDOW,0),this.D.set(t.SYNTHESIS_WINDOW,512),this.V=new Float32Array(1024),this.U=new Int32Array(32),this.VPos=0,this.allocation=[new Array(32),new Array(32)],this.scaleFactorInfo=[new Uint8Array(32),new Uint8Array(32)],this.scaleFactor=[new Array(32),new Array(32)],this.sample=[new Array(32),new Array(32)];for(var s=0;s<2;s++)for(var o=0;o<32;o++)this.scaleFactor[s][o]=[0,0,0],this.sample[s][o]=[0,0,0]};return t.prototype=Object.create(F.Decoder.Base.prototype),t.prototype.constructor=t,t.prototype.decode=function(){var t=this.bits.index>>3;if(t>=this.bits.byteLength)return!1;var e=this.decodeFrame(this.left,this.right);return this.bits.index=t+e<<3,!!e&&(this.destination&&(this.decodedFrameCnt++,this.destination.play(this.sampleRate,this.left,this.right)),this.advanceDecodedTime(this.left.length/this.sampleRate),!0)},t.prototype.getCurrentTime=function(){var t=this.destination?this.destination.enqueuedTime:0;return this.decodedTime-t},t.prototype.decodeFrame=function(e,i){var r=this.bits.read(11),s=this.bits.read(2),o=this.bits.read(2),n=!this.bits.read(1);if(r!==t.FRAME_SYNC||s!==t.VERSION.MPEG_1||o!==t.LAYER.II)return 0;var a=this.bits.read(4)-1;if(a>13)return 0;var h=this.bits.read(2),l=t.SAMPLE_RATE[h];if(3===h)return 0;s===t.VERSION.MPEG_2&&(h+=4,a+=14);var d=this.bits.read(1),u=(this.bits.read(1),this.bits.read(2)),c=0;u===t.MODE.JOINT_STEREO?c=this.bits.read(2)+1<<2:(this.bits.skip(2),c=u===t.MODE.MONO?0:32),this.bits.skip(4),n&&this.bits.skip(16);var p=144e3*t.BIT_RATE[a]/(l=t.SAMPLE_RATE[h])+d|0,g=0,f=0;if(s===t.VERSION.MPEG_2)g=2,f=30;else{var m=u===t.MODE.MONO?0:1,b=t.QUANT_LUT_STEP_1[m][a];f=63&(g=t.QUANT_LUT_STEP_2[b][h]),g>>=6}c>f&&(c=f);for(v=0;v<c;v++)this.allocation[0][v]=this.readAllocation(v,g),this.allocation[1][v]=this.readAllocation(v,g);for(v=c;v<f;v++)this.allocation[0][v]=this.allocation[1][v]=this.readAllocation(v,g);for(var y=u===t.MODE.MONO?1:2,v=0;v<f;v++){for(x=0;x<y;x++)this.allocation[x][v]&&(this.scaleFactorInfo[x][v]=this.bits.read(2));u===t.MODE.MONO&&(this.scaleFactorInfo[1][v]=this.scaleFactorInfo[0][v])}for(v=0;v<f;v++){for(x=0;x<y;x++)if(this.allocation[x][v]){var T=this.scaleFactor[x][v];switch(this.scaleFactorInfo[x][v]){case 0:T[0]=this.bits.read(6),T[1]=this.bits.read(6),T[2]=this.bits.read(6);break;case 1:T[0]=T[1]=this.bits.read(6),T[2]=this.bits.read(6);break;case 2:T[0]=T[1]=T[2]=this.bits.read(6);break;case 3:T[0]=this.bits.read(6),T[1]=T[2]=this.bits.read(6)}}u===t.MODE.MONO&&(this.scaleFactor[1][v][0]=this.scaleFactor[0][v][0],this.scaleFactor[1][v][1]=this.scaleFactor[0][v][1],this.scaleFactor[1][v][2]=this.scaleFactor[0][v][2])}for(var w=0,C=0;C<3;C++)for(var _=0;_<4;_++){for(v=0;v<c;v++)this.readSamples(0,v,C),this.readSamples(1,v,C);for(v=c;v<f;v++)this.readSamples(0,v,C),this.sample[1][v][0]=this.sample[0][v][0],this.sample[1][v][1]=this.sample[0][v][1],this.sample[1][v][2]=this.sample[0][v][2];for(v=f;v<32;v++)this.sample[0][v][0]=0,this.sample[0][v][1]=0,this.sample[0][v][2]=0,this.sample[1][v][0]=0,this.sample[1][v][1]=0,this.sample[1][v][2]=0;for(var E=0;E<3;E++){this.VPos=this.VPos-64&1023;for(var x=0;x<2;x++){t.MatrixTransform(this.sample[x],E,this.V,this.VPos),F.Fill(this.U,0);for(var R=512-(this.VPos>>1),A=this.VPos%128>>1;A<1024;){for(S=0;S<32;++S)this.U[S]+=this.D[R++]*this.V[A++];A+=96,R+=32}for(A=1120-A,R-=480;A<1024;){for(var S=0;S<32;++S)this.U[S]+=this.D[R++]*this.V[A++];A+=96,R+=32}for(var k=0===x?e:i,P=0;P<32;P++)k[w+P]=this.U[P]/2147418112}w+=32}}return this.sampleRate=l,p},t.prototype.readAllocation=function(e,i){var r=t.QUANT_LUT_STEP_3[i][e],s=t.QUANT_LUT_STEP4[15&r][this.bits.read(r>>4)];return s?t.QUANT_TAB[s-1]:0},t.prototype.readSamples=function(e,i,r){var s=this.allocation[e][i],o=this.scaleFactor[e][i][r],n=this.sample[e][i],a=0;if(s){if(63===o)o=0;else{var h=o/3|0;o=t.SCALEFACTOR_BASE[o%3]+(1<<h>>1)>>h}var l=s.levels;s.group?(a=this.bits.read(s.bits),n[0]=a%l,a=a/l|0,n[1]=a%l,n[2]=a/l|0):(n[0]=this.bits.read(s.bits),n[1]=this.bits.read(s.bits),n[2]=this.bits.read(s.bits));var d=65536/(l+1)|0;a=((l=(l+1>>1)-1)-n[0])*d,n[0]=a*(o>>12)+(a*(4095&o)+2048>>12)>>12,a=(l-n[1])*d,n[1]=a*(o>>12)+(a*(4095&o)+2048>>12)>>12,a=(l-n[2])*d,n[2]=a*(o>>12)+(a*(4095&o)+2048>>12)>>12}else n[0]=n[1]=n[2]=0},t.MatrixTransform=function(t,e,i,r){var s,o,n,a,h,l,d,u,c,p,g,f,m,b,y,v,T,w,C,_,E,x,R,A,S,k,P,D,L,I,B,F,U;s=t[0][e]+t[31][e],o=.500602998235*(t[0][e]-t[31][e]),n=t[1][e]+t[30][e],a=.505470959898*(t[1][e]-t[30][e]),h=t[2][e]+t[29][e],l=.515447309923*(t[2][e]-t[29][e]),d=t[3][e]+t[28][e],u=.53104259109*(t[3][e]-t[28][e]),c=t[4][e]+t[27][e],p=.553103896034*(t[4][e]-t[27][e]),g=t[5][e]+t[26][e],f=.582934968206*(t[5][e]-t[26][e]),m=t[6][e]+t[25][e],b=.622504123036*(t[6][e]-t[25][e]),y=t[7][e]+t[24][e],v=.674808341455*(t[7][e]-t[24][e]),T=t[8][e]+t[23][e],w=.744536271002*(t[8][e]-t[23][e]),C=t[9][e]+t[22][e],_=.839349645416*(t[9][e]-t[22][e]),E=t[10][e]+t[21][e],x=.972568237862*(t[10][e]-t[21][e]),R=t[11][e]+t[20][e],A=1.16943993343*(t[11][e]-t[20][e]),S=t[12][e]+t[19][e],k=1.48416461631*(t[12][e]-t[19][e]),P=t[13][e]+t[18][e],D=2.05778100995*(t[13][e]-t[18][e]),L=t[14][e]+t[17][e],I=3.40760841847*(t[14][e]-t[17][e]),U=s+(B=t[15][e]+t[16][e]),B=.502419286188*(s-B),s=n+L,L=.52249861494*(n-L),n=h+P,P=.566944034816*(h-P),h=d+S,S=.64682178336*(d-S),d=c+R,R=.788154623451*(c-R),c=g+E,E=1.06067768599*(g-E),g=m+C,C=1.72244709824*(m-C),m=y+T,T=5.10114861869*(y-T),y=U+m,m=.509795579104*(U-m),U=s+g,s=.601344886935*(s-g),g=n+c,c=.899976223136*(n-c),n=h+d,d=2.56291544774*(h-d),h=y+n,y=.541196100146*(y-n),n=U+g,g=1.30656296488*(U-g),U=h+n,h=.707106781187*(h-n),n=y+g,n+=y=.707106781187*(y-g),g=m+d,m=.541196100146*(m-d),d=s+c,c=1.30656296488*(s-c),s=g+d,d=.707106781187*(g-d),g=m+c,s+=g+=m=.707106781187*(m-c),g+=d,d+=m,c=B+T,B=.509795579104*(B-T),T=L+C,L=.601344886935*(L-C),C=P+E,E=.899976223136*(P-E),P=S+R,R=2.56291544774*(S-R),S=c+P,c=.541196100146*(c-P),P=T+C,C=1.30656296488*(T-C),T=S+P,P=.707106781187*(S-P),S=c+C,C=.707106781187*(c-C),c=B+R,B=.541196100146*(B-R),R=L+E,E=1.30656296488*(L-E),L=c+R,R=.707106781187*(c-R),c=B+E,T+=L+=c+=B=.707106781187*(B-E),L+=S+=C,S+=c+=R,c+=P,P+=R+=B,R+=C,C+=B,E=o+(F=10.1900081235*(t[15][e]-t[16][e])),o=.502419286188*(o-F),F=a+I,a=.52249861494*(a-I),I=l+D,D=.566944034816*(l-D),l=u+k,u=.64682178336*(u-k),k=p+A,p=.788154623451*(p-A),A=f+x,x=1.06067768599*(f-x),f=b+_,_=1.72244709824*(b-_),b=v+w,v=5.10114861869*(v-w),w=E+b,b=.509795579104*(E-b),E=F+f,F=.601344886935*(F-f),f=I+A,A=.899976223136*(I-A),I=l+k,k=2.56291544774*(l-k),l=w+I,w=.541196100146*(w-I),I=E+f,f=1.30656296488*(E-f),E=l+I,I=.707106781187*(l-I),l=w+f,f=.707106781187*(w-f),w=b+k,k=.541196100146*(b-k),b=F+A,A=1.30656296488*(F-A),F=w+b,b=.707106781187*(w-b),w=k+A,F+=w+=A=.707106781187*(k-A),w+=b,k=b+A,b=o+v,o=.509795579104*(o-v),v=a+_,a=.601344886935*(a-_),_=D+x,x=.899976223136*(D-x),D=u+p,p=2.56291544774*(u-p),u=b+D,b=.541196100146*(b-D),D=v+_,_=1.30656296488*(v-_),v=u+D,D=.707106781187*(u-D),u=b+_,_=.707106781187*(b-_),b=o+p,o=.541196100146*(o-p),p=a+x,x=1.30656296488*(a-x),a=b+p,p=.707106781187*(b-p),b=o+x,E+=v+=a+=b+=o=.707106781187*(o-x),v+=F,F+=a+=u+=_,a+=l+=f,l+=u+=b+=p,u+=w,w+=b+=D,b+=I,I+=D+=p+=o,D+=k,k+=p+=_,p+=f,f+=_+=o,_+=A,A+=o,i[r+48]=-U,i[r+49]=i[r+47]=-E,i[r+50]=i[r+46]=-T,i[r+51]=i[r+45]=-v,i[r+52]=i[r+44]=-s,i[r+53]=i[r+43]=-F,i[r+54]=i[r+42]=-L,i[r+55]=i[r+41]=-a,i[r+56]=i[r+40]=-n,i[r+57]=i[r+39]=-l,i[r+58]=i[r+38]=-S,i[r+59]=i[r+37]=-u,i[r+60]=i[r+36]=-g,i[r+61]=i[r+35]=-w,i[r+62]=i[r+34]=-c,i[r+63]=i[r+33]=-b,i[r+32]=-h,i[r+0]=h,i[r+31]=-I,i[r+1]=I,i[r+30]=-P,i[r+2]=P,i[r+29]=-D,i[r+3]=D,i[r+28]=-d,i[r+4]=d,i[r+27]=-k,i[r+5]=k,i[r+26]=-R,i[r+6]=R,i[r+25]=-p,i[r+7]=p,i[r+24]=-y,i[r+8]=y,i[r+23]=-f,i[r+9]=f,i[r+22]=-C,i[r+10]=C,i[r+21]=-_,i[r+11]=_,i[r+20]=-m,i[r+12]=m,i[r+19]=-A,i[r+13]=A,i[r+18]=-B,i[r+14]=B,i[r+17]=-o,i[r+15]=o,i[r+16]=0},t.FRAME_SYNC=2047,t.VERSION={MPEG_2_5:0,MPEG_2:2,MPEG_1:3},t.LAYER={III:1,II:2,I:3},t.MODE={STEREO:0,JOINT_STEREO:1,DUAL_CHANNEL:2,MONO:3},t.SAMPLE_RATE=new Uint16Array([44100,48e3,32e3,0,22050,24e3,16e3,0]),t.BIT_RATE=new Uint16Array([32,48,56,64,80,96,112,128,160,192,224,256,320,384,8,16,24,32,40,48,56,64,80,96,112,128,144,160]),t.SCALEFACTOR_BASE=new Uint32Array([33554432,26632170,21137968]),t.SYNTHESIS_WINDOW=new Float32Array([0,-.5,-.5,-.5,-.5,-.5,-.5,-1,-1,-1,-1,-1.5,-1.5,-2,-2,-2.5,-2.5,-3,-3.5,-3.5,-4,-4.5,-5,-5.5,-6.5,-7,-8,-8.5,-9.5,-10.5,-12,-13,-14.5,-15.5,-17.5,-19,-20.5,-22.5,-24.5,-26.5,-29,-31.5,-34,-36.5,-39.5,-42.5,-45.5,-48.5,-52,-55.5,-58.5,-62.5,-66,-69.5,-73.5,-77,-80.5,-84.5,-88,-91.5,-95,-98,-101,-104,106.5,109,111,112.5,113.5,114,114,113.5,112,110.5,107.5,104,100,94.5,88.5,81.5,73,63.5,53,41.5,28.5,14.5,-1,-18,-36,-55.5,-76.5,-98.5,-122,-147,-173.5,-200.5,-229.5,-259.5,-290.5,-322.5,-355.5,-389.5,-424,-459.5,-495.5,-532,-568.5,-605,-641.5,-678,-714,-749,-783.5,-817,-849,-879.5,-908.5,-935,-959.5,-981,-1000.5,-1016,-1028.5,-1037.5,-1042.5,-1043.5,-1040,-1031.5,1018.5,1e3,976,946.5,911,869.5,822,767.5,707,640,565.5,485,397,302.5,201,92.5,-22.5,-144,-272.5,-407,-547.5,-694,-846,-1003,-1165,-1331.5,-1502,-1675.5,-1852.5,-2031.5,-2212.5,-2394,-2576.5,-2758.5,-2939.5,-3118.5,-3294.5,-3467.5,-3635.5,-3798.5,-3955,-4104.5,-4245.5,-4377.5,-4499,-4609.5,-4708,-4792.5,-4863.5,-4919,-4958,-4979.5,-4983,-4967.5,-4931.5,-4875,-4796,-4694.5,-4569.5,-4420,-4246,-4046,-3820,-3567,3287,2979.5,2644,2280.5,1888,1467.5,1018.5,541,35,-499,-1061,-1650,-2266.5,-2909,-3577,-4270,-4987.5,-5727.5,-6490,-7274,-8077.5,-8899.5,-9739,-10594.5,-11464.5,-12347,-13241,-14144.5,-15056,-15973.5,-16895.5,-17820,-18744.5,-19668,-20588,-21503,-22410.5,-23308.5,-24195,-25068.5,-25926.5,-26767,-27589,-28389,-29166.5,-29919,-30644.5,-31342,-32009.5,-32645,-33247,-33814.5,-34346,-34839.5,-35295,-35710,-36084.5,-36417.5,-36707.5,-36954,-37156.5,-37315,-37428,-37496,37519,37496,37428,37315,37156.5,36954,36707.5,36417.5,36084.5,35710,35295,34839.5,34346,33814.5,33247,32645,32009.5,31342,30644.5,29919,29166.5,28389,27589,26767,25926.5,25068.5,24195,23308.5,22410.5,21503,20588,19668,18744.5,17820,16895.5,15973.5,15056,14144.5,13241,12347,11464.5,10594.5,9739,8899.5,8077.5,7274,6490,5727.5,4987.5,4270,3577,2909,2266.5,1650,1061,499,-35,-541,-1018.5,-1467.5,-1888,-2280.5,-2644,-2979.5,3287,3567,3820,4046,4246,4420,4569.5,4694.5,4796,4875,4931.5,4967.5,4983,4979.5,4958,4919,4863.5,4792.5,4708,4609.5,4499,4377.5,4245.5,4104.5,3955,3798.5,3635.5,3467.5,3294.5,3118.5,2939.5,2758.5,2576.5,2394,2212.5,2031.5,1852.5,1675.5,1502,1331.5,1165,1003,846,694,547.5,407,272.5,144,22.5,-92.5,-201,-302.5,-397,-485,-565.5,-640,-707,-767.5,-822,-869.5,-911,-946.5,-976,-1e3,1018.5,1031.5,1040,1043.5,1042.5,1037.5,1028.5,1016,1000.5,981,959.5,935,908.5,879.5,849,817,783.5,749,714,678,641.5,605,568.5,532,495.5,459.5,424,389.5,355.5,322.5,290.5,259.5,229.5,200.5,173.5,147,122,98.5,76.5,55.5,36,18,1,-14.5,-28.5,-41.5,-53,-63.5,-73,-81.5,-88.5,-94.5,-100,-104,-107.5,-110.5,-112,-113.5,-114,-114,-113.5,-112.5,-111,-109,106.5,104,101,98,95,91.5,88,84.5,80.5,77,73.5,69.5,66,62.5,58.5,55.5,52,48.5,45.5,42.5,39.5,36.5,34,31.5,29,26.5,24.5,22.5,20.5,19,17.5,15.5,14.5,13,12,10.5,9.5,8.5,8,7,6.5,5.5,5,4.5,4,3.5,3.5,3,2.5,2.5,2,2,1.5,1.5,1,1,1,1,.5,.5,.5,.5,.5,.5]),t.QUANT_LUT_STEP_1=[[0,0,1,1,1,2,2,2,2,2,2,2,2,2],[0,0,0,0,0,0,1,1,1,2,2,2,2,2]],t.QUANT_TAB={A:91,B:94,C:8,D:12},t.QUANT_LUT_STEP_2=[[t.QUANT_TAB.C,t.QUANT_TAB.C,t.QUANT_TAB.D],[t.QUANT_TAB.A,t.QUANT_TAB.A,t.QUANT_TAB.A],[t.QUANT_TAB.B,t.QUANT_TAB.A,t.QUANT_TAB.B]],t.QUANT_LUT_STEP_3=[[68,68,52,52,52,52,52,52,52,52,52,52],[67,67,67,66,66,66,66,66,66,66,66,49,49,49,49,49,49,49,49,49,49,49,49,32,32,32,32,32,32,32],[69,69,69,69,52,52,52,52,52,52,52,36,36,36,36,36,36,36,36,36,36,36,36,36,36,36,36,36,36,36]],t.QUANT_LUT_STEP4=[[0,1,2,17],[0,1,2,3,4,5,6,17],[0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,17],[0,1,3,5,6,7,8,9,10,11,12,13,14,15,16,17],[0,1,2,4,5,6,7,8,9,10,11,12,13,14,15,17],[0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15]],t.QUANT_TAB=[{levels:3,group:1,bits:5},{levels:5,group:1,bits:7},{levels:7,group:0,bits:3},{levels:9,group:1,bits:10},{levels:15,group:0,bits:4},{levels:31,group:0,bits:5},{levels:63,group:0,bits:6},{levels:127,group:0,bits:7},{levels:255,group:0,bits:8},{levels:511,group:0,bits:9},{levels:1023,group:0,bits:10},{levels:2047,group:0,bits:11},{levels:4095,group:0,bits:12},{levels:8191,group:0,bits:13},{levels:16383,group:0,bits:14},{levels:32767,group:0,bits:15},{levels:65535,group:0,bits:16}],t}(),F.Renderer.WebGL=function(){var t=function(e){this.canvas=e.canvas,this.canvas.width=this.canvas.clientWidth,this.canvas.height=this.canvas.clientHeight,this.width=this.canvas.width,this.height=this.canvas.height,this.fixWidth=this.width,this.enabled=!0,this._error=void 0,Object.defineProperty(this,"error",{get:this.getRenderError});var i={preserveDrawingBuffer:!!e.preserveDrawingBuffer,alpha:!1,depth:!1,stencil:!1,antialias:!1};if(this.gl=this.canvas.getContext("webgl",i)||this.canvas.getContext("experimental-webgl",i),!this.gl)throw new Error("Failed to get WebGL Context");var r=this.gl;try{this.bufferConvertVertex=r.createBuffer();var s=new Float32Array([0,0,0,1,1,0,1,1]);r.bindBuffer(r.ARRAY_BUFFER,this.bufferConvertVertex),r.bufferData(r.ARRAY_BUFFER,s,r.STATIC_DRAW),r.bindBuffer(r.ARRAY_BUFFER,null),this.convertProgram=this.createProgram(t.SHADER.VERTEX_IDENTITY,t.SHADER.FRAGMENT_YCRCB_TO_RGBA),this.attrConvertVertex=r.getAttribLocation(this.convertProgram,"vertex"),this.uniformY=r.getUniformLocation(this.convertProgram,"textureY"),this.uniformCb=r.getUniformLocation(this.convertProgram,"textureCb"),this.uniformCr=r.getUniformLocation(this.convertProgram,"textureCr"),this.textureY=this.createTexture(0,"textureY",this.convertProgram),this.textureCb=this.createTexture(1,"textureCb",this.convertProgram),this.textureCr=this.createTexture(2,"textureCr",this.convertProgram),this.loadingProgram=this.createProgram(t.SHADER.VERTEX_IDENTITY,t.SHADER.FRAGMENT_LOADING),this.bufferBlitVertex=r.createBuffer(),this.bufferBlitTexCoord=r.createBuffer(),this.blitProgram=this.createProgram(t.SHADER.VERTEX_BLIT,t.SHADER.FRAGMENT_BLIT),this.attrBlitVertex=r.getAttribLocation(this.blitProgram,"vertex"),this.attrBlitTexCoord=r.getAttribLocation(this.blitProgram,"texCoord"),this.uniformRgb=r.getUniformLocation(this.blitProgram,"textureRgb"),this.fb=r.createFramebuffer()}catch(t){this._error=t}this.shouldCreateUnclampedViews=!this.allowsClampedTextureData(),this.viewMode=e.viewMode||0};return t.prototype.destroy=function(){var t=this.gl;t.deleteTexture(this.textureY),t.deleteTexture(this.textureCb),t.deleteTexture(this.textureCr),void 0!==this.textureRgb&&(t.deleteTexture(this.textureRgb),this.textureRgb=void 0),t.deleteProgram(this.convertProgram),t.deleteProgram(this.loadingProgram),t.deleteProgram(this.blitProgram),t.deleteBuffer(this.bufferConvertVertex),t.deleteBuffer(this.bufferBlitVertex),t.deleteBuffer(this.bufferBlitTexCoord),t.deleteFramebuffer(this.fb)},t.prototype.resize=function(t,e){if(void 0===this._error){this.width=0|t,this.height=0|e,this.fixWidth=this.width+15>>4<<4,console.info("width:",t),console.info("height:",e);var i=this.gl;i.bindFramebuffer(i.FRAMEBUFFER,this.fb),void 0!==this.textureRgb&&(i.deleteTexture(this.textureRgb),this.textureRgb=void 0),this.textureRgb=this.createTexture(3,"textureRgb",this.blitProgram),i.texImage2D(i.TEXTURE_2D,0,i.RGB,this.fixWidth,this.height,0,i.RGB,i.UNSIGNED_BYTE,null),i.framebufferTexture2D(i.FRAMEBUFFER,i.COLOR_ATTACHMENT0,i.TEXTURE_2D,this.textureRgb,0);var r=1,s=1,o=this.width/this.height,n=this.canvas.width/this.canvas.height;0===this.viewMode?o>n?s=this.canvas.width/o/this.canvas.height:r=this.canvas.height*o/this.canvas.width:1===this.viewMode?o>n?r=this.canvas.height*o/this.canvas.width:s=this.canvas.width/o/this.canvas.height:this.viewMode,i.bindBuffer(i.ARRAY_BUFFER,this.bufferBlitVertex);var a=new Float32Array([-1*r,-1*s,r,-1*s,-1*r,s,r,s]);i.bufferData(i.ARRAY_BUFFER,a,i.STATIC_DRAW),i.bindBuffer(i.ARRAY_BUFFER,null);var h=this.width/this.fixWidth;i.bindBuffer(i.ARRAY_BUFFER,this.bufferBlitTexCoord);var l=new Float32Array([0,0,h,0,0,1,h,1]);i.bufferData(i.ARRAY_BUFFER,l,i.STATIC_DRAW),i.bindBuffer(i.ARRAY_BUFFER,null)}},t.prototype.createTexture=function(t,e,i){var r=this.gl,s=r.createTexture();return r.bindTexture(r.TEXTURE_2D,s),r.texParameteri(r.TEXTURE_2D,r.TEXTURE_MAG_FILTER,r.LINEAR),r.texParameteri(r.TEXTURE_2D,r.TEXTURE_MIN_FILTER,r.LINEAR),r.texParameteri(r.TEXTURE_2D,r.TEXTURE_WRAP_S,r.CLAMP_TO_EDGE),r.texParameteri(r.TEXTURE_2D,r.TEXTURE_WRAP_T,r.CLAMP_TO_EDGE),s},t.prototype.createProgram=function(t,e){var i=this.gl,r=i.createProgram();return i.attachShader(r,this.compileShader(i.VERTEX_SHADER,t)),i.attachShader(r,this.compileShader(i.FRAGMENT_SHADER,e)),i.linkProgram(r),i.useProgram(r),r},t.prototype.compileShader=function(t,e){var i=this.gl,r=i.createShader(t);if(i.shaderSource(r,e),i.compileShader(r),!i.getShaderParameter(r,i.COMPILE_STATUS))throw new Error(i.getShaderInfoLog(r));return r},t.prototype.allowsClampedTextureData=function(){var t=this.gl,e=t.createTexture();return t.bindTexture(t.TEXTURE_2D,e),t.texImage2D(t.TEXTURE_2D,0,t.LUMINANCE,1,1,0,t.LUMINANCE,t.UNSIGNED_BYTE,new Uint8ClampedArray([0])),0===t.getError()},t.prototype.renderProgress=function(t){return},t.prototype.render=function(t,e,i){if(this.enabled&&void 0===this._error){var r=this.gl,s=this.fixWidth,o=this.height,n=s>>1,a=o>>1;r.bindFramebuffer(r.FRAMEBUFFER,this.fb),r.viewport(0,0,this.fixWidth,this.height),r.clearColor(0,0,0,1),r.clear(r.COLOR_BUFFER_BIT),r.useProgram(this.convertProgram),this.shouldCreateUnclampedViews&&(t=new Uint8Array(t.buffer),e=new Uint8Array(e.buffer),i=new Uint8Array(i.buffer)),this.updateTexture(r.TEXTURE0,this.textureY,s,o,t),this.updateTexture(r.TEXTURE1,this.textureCb,n,a,e),this.updateTexture(r.TEXTURE2,this.textureCr,n,a,i),r.uniform1i(this.uniformY,0),r.uniform1i(this.uniformCb,1),r.uniform1i(this.uniformCr,2),r.bindBuffer(r.ARRAY_BUFFER,this.bufferConvertVertex),r.enableVertexAttribArray(this.attrConvertVertex),r.vertexAttribPointer(this.attrConvertVertex,2,r.FLOAT,!1,0,0),r.drawArrays(r.TRIANGLE_STRIP,0,4),r.bindBuffer(r.ARRAY_BUFFER,null),r.bindFramebuffer(r.FRAMEBUFFER,null),r.viewport(0,0,this.canvas.width,this.canvas.height),r.clearColor(0,0,0,1),r.clear(r.COLOR_BUFFER_BIT),r.useProgram(this.blitProgram),r.activeTexture(r.TEXTURE3),r.bindTexture(r.TEXTURE_2D,this.textureRgb),r.uniform1i(this.uniformRgb,3),r.bindBuffer(r.ARRAY_BUFFER,this.bufferBlitVertex),r.enableVertexAttribArray(this.attrBlitVertex),r.vertexAttribPointer(this.attrBlitVertex,2,r.FLOAT,!1,0,0),r.bindBuffer(r.ARRAY_BUFFER,this.bufferBlitTexCoord),r.enableVertexAttribArray(this.attrBlitTexCoord),r.vertexAttribPointer(this.attrBlitTexCoord,2,r.FLOAT,!1,0,0),r.drawArrays(r.TRIANGLE_STRIP,0,4),r.bindBuffer(r.ARRAY_BUFFER,null)}},t.prototype.updateTexture=function(t,e,i,r,s){var o=this.gl;o.activeTexture(t),o.bindTexture(o.TEXTURE_2D,e),o.texImage2D(o.TEXTURE_2D,0,o.LUMINANCE,i,r,0,o.LUMINANCE,o.UNSIGNED_BYTE,s)},t.prototype.getRenderError=function(){return this._error},t.prototype.enable=function(t){this.enabled=t},t.IsSupported=function(){try{if(!window.WebGLRenderingContext)return!1;var t=document.createElement("canvas");return!(!t.getContext("webgl")&&!t.getContext("experimental-webgl"))}catch(t){return!1}},t.SHADER={FRAGMENT_YCRCB_TO_RGBA:["precision mediump float;","uniform sampler2D textureY;","uniform sampler2D textureCb;","uniform sampler2D textureCr;","varying vec2 texCoord;","mat4 rec601 = mat4(","1.16438,  0.00000,  1.59603, -0.87079,","1.16438, -0.39176, -0.81297,  0.52959,","1.16438,  2.01723,  0.00000, -1.08139,","0, 0, 0, 1",");","void main() {","float y = texture2D(textureY, texCoord).r;","float cb = texture2D(textureCb, texCoord).r;","float cr = texture2D(textureCr, texCoord).r;","gl_FragColor = vec4(y, cr, cb, 1.0) * rec601;","}"].join("\n"),FRAGMENT_LOADING:["precision mediump float;","uniform float progress;","varying vec2 texCoord;","void main() {","float c = ceil(progress-(1.0-texCoord.y));","gl_FragColor = vec4(c,c,c,1);","}"].join("\n"),VERTEX_IDENTITY:["attribute vec2 vertex;","varying vec2 texCoord;","void main() {","texCoord = vertex;","gl_Position = vec4((vertex * 2.0 - 1.0) * vec2(1, -1), 0.0, 1.0);","}"].join("\n"),FRAGMENT_BLIT:["precision mediump float;","uniform sampler2D textureRgb;","varying vec2 textureCoordinate;","void main() {","gl_FragColor = texture2D(textureRgb, textureCoordinate);","}"].join("\n"),VERTEX_BLIT:["attribute vec4 vertex;","attribute vec4 texCoord;","varying vec2 textureCoordinate;","void main() {","textureCoordinate = texCoord.xy;","gl_Position = vertex;","}"].join("\n")},t}(),F.Renderer.Canvas2D=function(){var t=function(t){this.canvas=t.canvas||document.createElement("canvas"),this.width=this.canvas.width,this.height=this.canvas.height,this.enabled=!0,Object.defineProperty(this,"error",{get:this.getRenderError});try{this.context=this.canvas.getContext("2d")}catch(t){this._error=t}this.viewMode=t.viewMode||0};return t.prototype.destroy=function(){},t.prototype.resize=function(t,e){this.width=0|t,this.height=0|e;var i=this.width,r=this.height,s=this.width/this.height,o=this.canvas.clientWidth/this.canvas.clientHeight;0===this.viewMode?s<o?i=r*o:r=i/o:1===this.viewMode?s<o?r=i/o:i=r*o:this.viewMode,console.info("imgAR:",s),console.info("viewAR:",o),console.info("viewWidth:",i),console.info("viewHeight:",r),this.canvas.width=i,this.canvas.height=r,this.imageData=this.context.createImageData(this.width,this.height),F.Fill(this.imageData.data,255)},t.prototype.renderProgress=function(t){var e=this.canvas.width,i=this.canvas.height,r=this.context;r.fillStyle="#222",r.fillRect(0,0,e,i),r.fillStyle="#fff",r.fillRect(0,i-i*t,e,i*t)},t.prototype.render=function(t,e,i){this.YCbCrToRGBA(t,e,i,this.imageData.data);var r=0,s=0;if(0===this.viewMode)r=(this.canvas.width-this.width)/2,s=(this.canvas.height-this.height)/2,this.context.fillStyle="black",this.context.fillRect(0,0,this.canvas.width,this.canvas.height),this.context.putImageData(this.imageData,r,s,0,0,this.width,this.height);else if(1===this.viewMode){r=(this.width-this.canvas.width)/2,s=(this.height-this.canvas.height)/2;this.context.putImageData(this.imageData,0,0,0,0,this.canvas.width,this.canvas.height)}else 2===this.viewMode&&this.context.putImageData(this.imageData,0,0)},t.prototype.YCbCrToRGBA=function(t,e,i,r){if(this.enabled)for(var s,o,n,a,h,l=this.width+15>>4<<4,d=l>>1,u=0,c=l,p=l+(l-this.width),g=0,f=d-(this.width>>1),m=0,b=4*this.width,y=4*this.width,v=this.width>>1,T=this.height>>1,w=0;w<T;w++){for(var C=0;C<v;C++){s=e[g],o=i[g],g++,n=s+(103*s>>8)-179,a=(88*o>>8)-44+(183*s>>8)-91,h=o+(198*o>>8)-227;var _=t[u++],E=t[u++];r[m]=_+n,r[m+1]=_-a,r[m+2]=_+h,r[m+4]=E+n,r[m+5]=E-a,r[m+6]=E+h,m+=8;var x=t[c++],R=t[c++];r[b]=x+n,r[b+1]=x-a,r[b+2]=x+h,r[b+4]=R+n,r[b+5]=R-a,r[b+6]=R+h,b+=8}u+=p,c+=p,m+=y,b+=y,g+=f}},t.prototype.getRenderError=function(){return this._error},t}(),F.AudioOutput.WebAudio=function(){var t=function(e){this.context=t.CachedContext=t.CachedContext||new(window.AudioContext||window.webkitAudioContext),this.gain=this.context.createGain(),this.destination=this.gain,this.gain.connect(this.context.destination),this.context._connections=(this.context._connections||0)+1,this.startTime=0,this.buffer=null,this.wallclockStartTime=0,this.volume=1,this.enabled=!0,this.unlocked=!t.NeedsUnlocking(),Object.defineProperty(this,"enqueuedTime",{get:this.getEnqueuedTime})};return t.prototype.destroy=function(){this.gain.disconnect(),this.context._connections--,0===this.context._connections&&(this.context.close(),t.CachedContext=null)},t.prototype.play=function(t,e,i){if(this.enabled){if(!this.unlocked){var r=F.Now();return this.wallclockStartTime<r&&(this.wallclockStartTime=r),void(this.wallclockStartTime+=e.length/t)}this.gain.gain.value=this.volume;var s=this.context.createBuffer(2,e.length,t);s.getChannelData(0).set(e),s.getChannelData(1).set(i);var o=this.context.createBufferSource();o.buffer=s,o.connect(this.destination);var n=this.context.currentTime,a=s.duration;this.startTime<n&&(this.startTime=n,this.wallclockStartTime=F.Now()),o.start(this.startTime),this.startTime+=a,this.wallclockStartTime+=a}},t.prototype.stop=function(){this.gain.gain.value=0},t.prototype.getEnqueuedTime=function(){return Math.max(this.wallclockStartTime-F.Now(),0)},t.prototype.resetEnqueuedTime=function(){this.startTime=this.context.currentTime,this.wallclockStartTime=F.Now()},t.prototype.unlock=function(t){if(this.unlocked)t&&t();else{this.unlockCallback=t;var e=this.context.createBuffer(1,1,22050),i=this.context.createBufferSource();i.buffer=e,i.connect(this.destination),i.start(0),setTimeout(this.checkIfUnlocked.bind(this,i,0),0)}},t.prototype.checkIfUnlocked=function(t,e){t.playbackState===t.PLAYING_STATE||t.playbackState===t.FINISHED_STATE?(this.unlocked=!0,this.unlockCallback&&(this.unlockCallback(),this.unlockCallback=null)):e<10&&setTimeout(this.checkIfUnlocked.bind(this,t,e+1),100)},t.NeedsUnlocking=function(){return/iPhone|iPad|iPod/i.test(navigator.userAgent)},t.IsSupported=function(){return window.AudioContext||window.webkitAudioContext},t.CachedContext=null,t}();var U={start:0,playing:1,stop:2},N={start:0,stop:1,error:2},M={cdn:0,ultra:1};o.prototype.setPlaySourceType=function(t){this.sourceType=t},o.prototype.startPlayingStream=function(t,e,i,r,o,a){this.logger.debug("zpc.sps.0 call");var h=this.playerList[t];if(h||this.stopPlayingStream(t),!(h=this.playerList[t]=new s(this.logger,t,e,i,(this.sourceType,2),r,o,a)))return this.logger.info("zpc.sps.0 new player failed"),!1;++this.playerCount;var l=n(this,h);return this.logger.debug("zpc.sps.0 call result:",l),l},o.prototype.stopPlayingStream=function(t){this.logger.debug("zpc.sps.1.0 call");var e=this.playerList[t];e&&(e.resetPlayer(),F.DestroySingleRenderer(e.view.id),delete this.playerList[t],void 0!==this.playerInfo[t]&&delete this.playerInfo[t],--this.playerCount,this.onPlayStateUpdate(N.stop,e.streamid)),a(this),this.logger.debug("zpc.sps.1.0 call success")},o.prototype.setPlayVolume=function(t,e){var i=this.playerList[t];return i&&i.player?i.player.setVolume(e):(this.logger.info("zpc.spv.0 no player"),!1)},o.prototype.reset=function(){this.logger.debug("zpc.r.0 call");for(var t in this.playerList){var e=this.playerList[t];e&&e.resetPlayer()}this.playerCount=0,this.playerList={},this.playerInfo={},a(this),F.DestroyAllRenderer(),this.logger.debug("zpc.r.0 call success")},o.prototype.enableDecode=function(t,e){this.logger.debug("zpc.ed.0 call"),this.playerInfo[t]=e;var i=this.playerList[t];if(i)if(i.state==U.playing){var r=i.enableDecode(e);this.logger.info("zpc.ed.0 player is playing, streamId="+t+" set decode "+e+r)}else this.logger.info("zpc.ed.0 player is not start palying, streamId= "+t+" set decode "+e);else this.logger.info("zpc.ed.0 cannot find player")},o.prototype.onPlayStateUpdate=function(t,e){},o.prototype.onPlayQualityUpdate=function(t,e){};var O={cdn:0,ultra:1},q={logout:0,trylogin:1,login:2},V={added:0,deleted:1},H={added:12001,deleted:12002,updated:12003},Y=5,z=[2e3,2e3,3e3,3e3,4e3],G=3,W=3e3,X="1.0.3";l.prototype.onPlayStateUpdateHandle=function(t,e){this.onPlayStateUpdate(t,e)},l.prototype.onPlayQualityUpdateHandle=function(t,e){this.onPlayQualityUpdate(t,e)},l.prototype.config=function(t){return this.logger.debug("zc.p.c.0 call"),this.appid=t.appid,this.server=t.server,this.idName=t.idName,this.nickName=t.nickName,this.logger.setLogLevel(t.logLevel),this.workerUrl=t.workerUrl,this.workerPlayerJSBlob=t.workerPlayerJSBlob,void 0!=t.remoteLogLevel?this.logger.setRemoteLogLevel(t.remoteLogLevel):this.logger.setRemoteLogLevel(0),this.logger.setSessionInfo(t.appid,"","",t.idName,""),this.logger.openLogServer(t.logUrl),this.configOK=!0,this.logger.debug("zc.p.c.0 call success"),!0},l.prototype.login=function(t,e,i,r,s){return this.logger.setSessionInfo(this.appid,t,"",this.idName,""),this.logger.info("zc.p.l.0 call:",t,i),this.configOK?(this.runState!==q.logout&&(this.logger.debug("zc.p.l.0 reset"),p(this,q.logout),E(this)),this.logger.debug("zc.p.l.0 begin"),p(this,q.trylogin),this.roomid=t,this.token=i,this.role=e,function(t,e,i){var r=function(){},s=function(){};i.success&&"function"==typeof i.success&&(r=i.success),i.error&&"function"==typeof i.error&&(s=i.error),t.callbackList[e+"SuccessCallback"]=r,t.callbackList[e+"ErrorCallback"]=s}(this,"login",{success:r,error:s}),w(this),C(this),this.logger.info("zc.p.l.0 call success"),!0):(this.logger.info("zc.p.l.0 param error"),!1)},l.prototype.logout=function(){return this.logger.debug("zc.p.l.1.0 call"),this.runState===q.logout?(this.logger.info("zc.p.l.1.0 at logout"),!1):(p(this,q.logout),E(this),this.logger.debug("zc.p.l.1.0 call success"),!0)},l.prototype.setUserStateUpdate=function(t){return this.logger.debug("zc.p.eusu.0 call"),"boolean"!=typeof t?(this.logger.info("zp.p.eusu.0 param error"),!1):(this.userStateUpdate=t,this.logger.debug("zc.p.eusu.0 call success "+t),!0)},l.prototype.sendCustomCommand=function(t,e,i,r){if(this.logger.debug("zc.p.scc.0 call"),this.runState!==q.login)return this.logger.info("zc.p.scc.0 state error"),!1;if(!(t&&t instanceof Array&&0!=t.length))return this.logger.info("zc.p.scc.0 dstMembers error"),!1;var s={dest_id_name:t,custom_msg:e};return m(this,"custommsg",s,i,r),this.logger.debug("zc.p.scc.0 call success"),!0},l.prototype.sendRoomMsg=function(t,e,i,r,s){if(this.logger.debug("srm.0 call"),this.runState===q.login){var o=Date.parse(new Date);if(this.sendRoomMsgTime>0&&this.sendRoomMsgTime+this.SendRoomMsgInterval>o)return this.logger.info("srm.0 freq error"),void(s&&s(Q.FREQ_LIMITED,0,t,e,i));this.sendRoomMsgTime=o,this.logger.debug("srm.0 send fetch request");m(this,"im_chat",{msg_category:t,msg_type:e,msg_content:i},r,s),this.logger.debug("srm.0 call success")}else this.logger.info("srm.0 state error")},l.prototype.startPlayingStream=function(t,e,i,r){i=i||1,this.logger.debug("zc.p.sps.0 call");var s="z0";"1"===r&&(s="z1"),this.logger.info("zc.p.sps.0 quality:"+s+" "+r);for(var o=null,n=0;n<this.streamList.length;n++)if(this.streamList[n].stream_id===t){if(this.streamList[n].quality!==s)break;o=this.streamList[n].urls_ws||[],this.streamList[n].viewMode=i;break}if(console.log("streamUrls = ",o),!o||o.length<=0)return this.logger.debug("zc.p.sps.0 fetch stream url"),this.mapStreamDom[t]=e,this.mapStreamQuality[t]=s,function(t,e,i){t.logger.debug("fsu.0 call"),t.runState===q.login?(t.logger.debug("fsu.0 send fetch request"),f(t,"stream_url",{stream_ids:[e],quality:i}),t.logger.debug("fsu.0 call success")):t.logger.info("fsu.0 state error")}(this,t,s),!1;this.logger.debug("zc.p.sps.0 play"),d(this,t,e)},l.prototype.stopPlayingStream=function(t){return this.logger.debug("zc.p.sps.1.0 call"),t&&""!==t?(this.playerCenter.stopPlayingStream(t),this.logger.debug("zc.p.sps.1.0 call success"),!0):(this.logger.info("zc.p.sps.1.0 param error"),!1)},l.prototype.setPlayVolume=function(t,e){return this.logger.debug("zc.p.spv.0 call"),this.playerCenter.setPlayVolume(t,e),this.logger.debug("zc.p.spv.0 call success"),!0},l.prototype.setPreferPlaySourceType=function(t){return this.logger.debug("zc.p.sppst.0 call"),"number"!=typeof t||t!==O.cdn&&t!==O.ultra?(this.logger.info("zc.p.sppst.0 param error"),!1):(this.preferPlaySourceType=t,this.playerCenter.setPlaySourceType(t),this.logger.debug("zc.p.sppst.0 call success"),!0)},l.prototype.release=function(){this.logger.debug("zc.p.r.0 call"),p(this,q.logout),E(this),this.playerCenter.reset(),this.logger.stopLogServer(),this.logger.debug("zc.p.r.0 call success")},l.prototype.enableDecode=function(t,e){if(this.logger.debug("zc.p.ed.0 call"),this.runState!=q.login)return this.logger.info("zc.p.ed.0 don't login in"),!1;for(var i=!1,r=0;r<this.streamList.length;r++)if(this.streamList[r].stream_id===t){i=!0;break}return i?(this.playerCenter.enableDecode(t,e),this.logger.debug("zc.p.ed.0 call success"),!0):(this.logger.info("zc.p.ed.0 cannot find stream"),!1)};for(var Q={SUCCESS:{code:"ZegoClient.Success",msg:"success."},PARAM:{code:"ZegoClient.Error.Param",msg:"input error."},HEARTBEAT_TIMEOUT:{code:"ZegoClient.Error.Timeout",msg:"heartbeat timeout."},LOGIN_TIMEOUT:{code:"ZegoClient.Error.Timeout",msg:"login timeout."},SEND_MSG_TIMEOUT:{code:"ZegoClient.Error.Timeout",msg:"send customsg timeout."},RESET_QUEUE:{code:"ZegoClient.Error.Timeout",msg:"msg waiting ack is clear when reset."},LOGIN_DISCONNECT:{code:"ZegoClient.Error.Network",msg:"network is broken and login fail."},KICK_OUT:{code:"ZegoClient.Error.Kickout",msg:"kickout reason="},UNKNOWN:{code:"ZegoClient.Error.Unknown",msg:"unknown error."},FREQ_LIMITED:{code:"ZegoClient.Error.requencyLimited",msg:"Frequency Limited."}},j=["onDisconnect","onKickOut","onRecvCustomCommand","onStreamUpdated","onStreamExtraInfoUpdated","onPlayStateUpdate","onRecvRoomMsg","onPlayQualityUpdate","onUserStateUpdate","onGetTotalUserList"],K=0;K<j.length;K++)l.prototype[j[K]]=function(){};return l});


/***/ }),
/* 22 */
/***/ (function(module, exports, __webpack_require__) {

!function(t,e){ true?module.exports=e():"function"==typeof define&&define.amd?define(e):t.ZegoClient=e()}(this,function(){"use strict";function t(t,e){this._id="number"==typeof t?t:null,this._data=e||null,this.next=null,this.prev=null}function e(){this.start=new t,this.end=new t,this.start.next=this.end,this.start.prev=null,this.end.prev=this.start,this.end.next=null,this._idCounter=0,this._numNodes=0}function i(){this.logSeq=0,this.logLevel=L.disable,this.logRemoteLevel=L.disable,this.websocket=null,this.url="",this.appid=0,this.sessionid="0",this.roomid="",this.userid="",this.userName="",this.logCache=[],this.logCacheSend=[],this.logCacheMax=100}function r(t,e,i){var r=new Date,s=1900+r.getYear()+"/";s+=(D[r.getMonth()+1]||r.getMonth()+1)+"/",s+=(D[r.getDate()]||r.getDate())+" ",s+=(D[r.getHours()]||r.getHours())+":",s+=(D[r.getMinutes()]||r.getMinutes())+":",s+=D[r.getSeconds()]||r.getSeconds(),s+="."+r.getTime()%1e3;var o=i.substr(0,i.indexOf(" "));0==o.length&&(o=i);var n=i.substr(i.indexOf(" ")+1);0==n.length&&(n="");var a={time:s,level:e,action:o,content:n,appid:t.appid,roomid:t.roomid,userid:t.userid,userName:t.userName,sessionid:t.sessionid};return[JSON.stringify(a)]}function s(t,e,i,r,s,o,n,a){this.streamid=e,this.viewMode=o,this.urls=i,this.playUrlIndex=0,this.playUrlTryCount=0,this.view=r,this.reconnectLimit=s,this.reconnectCount=0,this.state=I.stop,this.playerStat={videoBytes:0,videoFrameCnt:0,videoDecodeFrameCnt:0},this.player=null,this.stateTimeStamp=0,this.logger=t,this.firstCallback=!0,this.decode=!0,this.workerUrl=n,this.workerPlayerJSBlob=a}function o(t){this.sourceType=N.cdn,this.playerList={},this.playerCount=0,this.playerMonitorTimer=null,this.playerMonitorInterval=1e3,this.playerStartTimeout=5e3,this.playerStartBitrate=12500,this.playerStopBitrate=1250,this.playerStopTimeout=5e3,this.logger=t,this.playerInfo={}}function n(t,e){for(;e.playUrlTryCount<e.urls.length;)if(++e.reconnectCount>e.reconnectLimit)e.playUrlTryCount++,e.playUrlIndex=(e.playUrlIndex+1)%e.urls.length,e.reconnectCount=0;else if(t.logger.info("tsp.0 streamid: "+e.streamid+", index:"+e.playUrlIndex+", url:"+e.getCurrentPlayerUrl()),e.newPlayer(t,t.sourceType))break;return e.playUrlTryCount>=e.urls.length&&(t.logger.info("tsp.1 stream: ",e.streamid),e.resetPlayer(),P.DestroySingleRenderer(e.view.id),delete t.playerList[e.streamid],--t.playerCount,t.onPlayStateUpdate(F.error,e.streamid)),a(t),!0}function a(t){t.playerCount>0?t.playerMonitorTimer||(t.logger.debug("upm.1 called"),t.playerMonitorTimer=setInterval(function(){!function(t){var e,i,r,s,o=Date.now(),l=0;for(var d in t.playerList)if(e=t.playerList[d]){if(i=e.player.playoutStatus.videoBytes-e.playerStat.videoBytes,r=e.player.playoutStatus.videoFrameCnt-e.playerStat.videoFrameCnt,s=e.player.playoutStatus.videoDecodeFrameCnt-e.playerStat.videoDecodeFrameCnt,l=o-e.stateTimeStamp,e.state===U.start)if(i>=l/1e3*t.playerStartBitrate&&s>0){e.state=U.playing,e.stateTimeStamp=o,e.playUrlTryCount=0,e.updatePlayerStat(),t.logger.info("ups.1 streamid: ",e.streamid),e.firstCallback&&(e.firstCallback=!1,t.onPlayStateUpdate(F.start,e.streamid));var u=t.playerInfo[d];void 0!==u?e.enableDecode(u):(t.logger.info("ups.1 don't set decode: "+e.streamid),u=!0),u&&h(t,e,i,r,s,l)}else l>=t.playerStartTimeout&&(t.logger.info("ups.2 streamid: "+e.streamid),n(t,e));else e.state===U.playing&&l>=t.playerStopTimeout&&e.decode&&(i<l/1e3*t.playerStopBitrate?(e.state=U.stop,e.stateTimeStamp=o,t.logger.info("ups.3 streamid: "+e.streamid),n(t,e)):(e.stateTimeStamp=o,e.updatePlayerStat(),e.decode&&h(t,e,i,r,s,l)));t.logger.debug("ups.0 streamid: "+d+": videoBytesAdd="+i+"videoFrameCntAdd="+r+"videoDecodeFrameCntAdd="+s)}else delete t.playerList[d];a(t)}(t)},t.playerMonitorInterval)):t.playerMonitorTimer&&(t.logger.debug("upm.2 called"),clearInterval(t.playerMonitorTimer),t.playerMonitorTimer=null)}function h(t,e,i,r,s,o){var n=s/(o/1e3),a=8*i/o,h={fps:n,kbs:a};t.onPlayQualityUpdate(e.streamid,h),t.logger.info("oups.1 streamid:"+e.streamid+"fps="+n+" kbs="+a)}function l(){this.appid=0,this.server="",this.idName="",this.nickName="",this.configOK=!1,this.logger=new i,this.userStateUpdate=!1,this.userSeq=0,this.userQuerying=!1,this.userTempList=[],this.roomid="",this.token="",this.role=0,this.callbackList={},this.runState=M.logout,this.lastRunState=M.logout,this.userid="",this.sessionid="",this.cmdSeq=0,this.websocket=null,this.globalHeader=null,this.tryLoginCount=0,this.tryLoginTimer=null,this.tryHeartbeatCount=0,this.tryHeartbeatTimer=null,this.heartbeatInterval=3e4,this.streamList=[],this.streamQuerying=!1,this.mapStreamDom={},this.mapStreamQuality={},this.preferPlaySourceType=B.cdn,this.wsPlayerList={},this.playerCenter=new o(this.logger),this.playerCenter.onPlayStateUpdate=this.onPlayStateUpdateHandle.bind(this),this.playerCenter.onPlayQualityUpdate=this.onPlayQualityUpdateHandle.bind(this),this.playerStateTimer=null,this.playerStateInterval=2e3,this.sendDataMap={},this.sendDataList=new e,this.sendDataCheckTimer=null,this.sendDataCheckInterval=2e3,this.sendDataTimeout=5e3,this.sendDataDropTimeout=1e4,this.sendDataCheckOnceCount=100,this.sendRoomMsgTime=0,this.SendRoomMsgInterval=500}function d(t,e,i){t.logger.debug("zc.p.dps.0 call");for(var r=null,s=0,o=0;o<t.streamList.length;o++)if(t.streamList[o].stream_id===e){r=t.streamList[o].urls_ws||[],s=t.streamList[o].viewMode;break}return!(!r||r.length<=0)&&(t.logger.info("zc.p.dps.0 streamid: "+e+" streamUrl: "+r),t.playerCenter.startPlayingStream(e,r,i,s,t.workerUrl,t.workerPlayerJSBlob),t.logger.debug("zc.p.dps.0 call success"),!0)}function u(t,e){return t.callbackList[e+"ErrorCallback"]}function c(t){var e={1:"parse json error.",1001:"login is processing.",1002:"liveroom request error.",1003:"zpush connect fail.",1004:"zpush handshake fail.",1005:"zpush login fail.",1006:"user login state is wrong.",1007:"got no zpush addr",1008:"token error",1009:"dispatch error",1e9:"liveroom cmd error, result="};if(0===t)return{code:"ZegoClient.Success",msg:"success"};var i={};return i.code="ZegoClient.Error.Server",i.msg=t>1e9?e[1e9]+t:void 0!=e[t]?e[t]:"unknown error code:"+t,i}function g(t,e){t.logger.debug("srs.0 old="+t.runState+", new="+e),t.lastRunState=t.runState,t.runState=e}function p(t,e){return t.globalHeader={Protocol:"req",cmd:e,appid:t.appid,seq:++t.cmdSeq,user_id:t.userid,session_id:t.sessionid,room_id:t.roomid},t.globalHeader}function f(t,e,i){if(t.logger.debug("sm.0 call "+e),t.websocket&&1===t.websocket.readyState){var r={header:p(t,e),body:i},s=JSON.stringify(r);t.websocket.send(s),t.logger.debug("sm.0 success")}else t.logger.info("sm.0 error")}function m(t,e,i,r,s){if(t.logger.debug("scm.0 call"),!t.websocket||1!==t.websocket.readyState)return t.logger.info("scm.0 error"),!1;var o=p(t,e),n={header:o,body:i},a=JSON.stringify(n);void 0==r&&(r=null),void 0==s&&(s=null);var h={data:n,seq:o.seq,deleted:!1,time:Date.parse(new Date),success:r,error:s},l=t.sendDataList.push(h);return t.sendDataMap[h.seq]=l,t.websocket.send(a),t.logger.debug("scm.0 success seq: ",o.seq),!0}function b(t){if(t.logger.debug("scmt.0 call"),t.runState===M.login){for(var e=t.sendDataList.getFirst(),i=Date.parse(new Date),r=0,s=0,o=0;!(null==e||e._data.time+t.sendDataTimeout>i||(delete t.sendDataMap[e._data.data.header.seq],t.sendDataList.remove(e),++s,null==e._data.error||t.sendDataDropTimeout>0&&e._data.time+t.sendDataDropTimeout>i?++o:e._data.error(W.SEND_MSG_TIMEOUT,e._data.data.header.seq,e._data.data.body.custom_msg),++r>=t.sendDataCheckOnceCount));)e=t.sendDataList.getFirst();t.sendDataCheckTimer=setTimeout(function(){b(t)},t.sendDataCheckInterval),t.logger.debug("scmt.0 call success, stat: timeout=",s,"drop=",o)}else t.logger.info("scmt.0 state error")}function y(t){t.logger.debug("rcm.0 call"),clearTimeout(t.sendDataCheckTimer),t.sendDataCheckTimer=null;for(var e=t.sendDataList.getFirst();null!=e;)t.sendDataList.remove(e),null!=e._data.error&&e._data.error(W.RESET_QUEUE,e._data.data.header.seq,e._data.data.body.custom_msg),e=t.sendDataList.getFirst();t.sendDataMap={},t.logger.debug("rcm.0 call success")}function v(t){t.logger.debug("rht.0 call"),clearTimeout(t.heartbeatTimer),t.heartbeatTimer=null,t.tryHeartbeatCount=0,t.logger.debug("rht.0 call success")}function T(t){if(t.logger.debug("sht.0 call"),t.runState===M.login){if(++t.tryHeartbeatCount>H)return t.logger.error("sht.0 come to try limit"),g(t,M.logout),S(t),void t.onDisconnect(W.HEARTBEAT_TIMEOUT);t.logger.debug("sht.0 send packet");f(t,"hb",{reserve:0}),t.heartbeatTimer=setTimeout(function(){T(t)},t.heartbeatInterval),t.logger.debug("sht.0 call success")}else t.logger.info("sht.0 state error")}function w(t){t.logger.debug("rtl.0 call"),clearTimeout(t.tryLoginTimer),t.tryLoginTimer=null,t.tryLoginCount=0,t.logger.debug("rtl.0 call success")}function _(t){if(t.logger.debug("tl.0 call"),t.runState===M.trylogin){if(++t.tryLoginCount>z){t.logger.error("tl.0 fail times limit");var e=t.lastRunState;return g(t,M.logout),S(t),void(e==M.login?(t.logger.info("tl.0 fail and disconnect"),t.onDisconnect(W.LOGIN_DISCONNECT)):(t.logger.info("tl.0 fail and callback user"),u(t,"login")(W.LOGIN_TIMEOUT)))}if(t.websocket&&1===t.websocket.readyState){t.logger.info("tl.0 use current websocket and sent login");var i=C(t);f(t,"login",i)}else{t.logger.debug("tl.0 need new websocket");try{t.websocket&&(t.logger.info("tl.0 close error websocket"),t.websocket.onclose=null,t.websocket.onerror=null,t.websocket.close(),t.websocket=null),t.logger.debug("tl.0 new websocket"),t.websocket=new WebSocket(t.server),t.websocket.onopen=function(){t.logger.info("tl.0 websocket.onpen call"),function(t){t.websocket.onmessage=function(e){var i=JSON.parse(e.data);if(t.logger.debug("jsonmsg=",JSON.parse(e.data)),"login"!==i.header.cmd)if(i.header.appid===t.appid&&i.header.session_id===t.sessionid&&i.header.user_id===t.userid&&i.header.room_id===t.roomid&&t.runState===M.login)switch(i.header.cmd){case"hb":!function(t,e){if(t.logger.debug("hhbr.0 call stream_seq "+e.body.stream_seq+" user_seq "+e.body.server_user_seq),0!==e.body.err_code){t.logger.info("hhbr.0 call disconnect, server error=",e.body.err_code),g(t,M.logout),S(t);var i=c(e.body.err_code);return void t.onDisconnect(i)}t.tryHeartbeatCount=0,t.heartbeatInterval=e.body.hearbeat_interval,t.heartbeatInterval<Y&&(t.heartbeatInterval=Y);e.body.stream_seq!==t.streamSeq&&(t.logger.info("hhbr.0 call update stream"),E(t));e.body.server_user_seq!==t.userSeq&&t.userStateUpdate&&(t.logger.info("hhbr.0 call update user "+e.body.server_user_seq,t.userSeq),A(t));t.logger.debug("hhbr.0 call success")}(t,i);break;case"logout":!function(t,e){t.logger.debug("hlor.0 result=",e.body.err_code)}(t,i);break;case"custommsg":!function(t,e){t.logger.debug("hscmr.0 call");var i,r=t.sendDataMap[e.header.seq];null!=r?("custommsg"!=(i=r._data).data.header.cmd?t.logger.error("hscmr.0 cmd wrong"+i.data.header.cmd):0===e.body.err_code?null!=i.success&&i.success(e.header.seq,i.data.body.custom_msg):null!=i.error&&i.error(c(e.body.err_code),e.header.seq,i.data.body.custom_msg),delete t.sendDataMap[e.header.seq],t.sendDataList.remove(r)):t.logger.error("hscmr.0 no found seq="+e.header.seq);t.logger.debug("hscmr.0 call success")}(t,i);break;case"stream_info":!function(t,e){if(t.logger.debug("hfslr.0 call"),t.streamQuerying=!1,0!==e.body.err_code)return void t.logger.info("hfslr.0 server error=",e.body.err_code);if(t.streamSeq===e.body.stream_seq)return void t.logger.info("hfslr.0 same seq");R(t,e.body.stream_seq,e.body.stream_info),t.logger.debug("hfslr.0 call success")}(t,i);break;case"push_custommsg":!function(t,e){var i=JSON.parse(e.body.custommsg);t.logger.debug("hpcm.0 submsg=",i),t.onRecvCustomCommand(i.from_userid,i.from_username,i.custom_content)}(t,i);break;case"push_stream_update":!function(t,e){if(t.logger.debug("hpsum.0 call"),!e.body.stream_info||0===e.body.stream_info.length)return void t.logger.info("hpsum.0, emtpy list");if(e.body.stream_info.length+t.streamSeq!==e.body.stream_seq)return t.logger.info("hpsum.0 call updatestream"),void E(t);switch(t.streamSeq=e.body.stream_seq,e.body.stream_cmd){case q.added:!function(t,e){t.logger.debug("hasl.0 call");for(var i,r=[],s=0;s<e.length;s++){i=!1;for(var o=0;o<t.streamList.length;o++)if(e[s].stream_id===t.streamList[o].stream_id){i=!0;break}i||r.push(e[s])}0!==r.length&&(t.logger.debug("hasl.0 callback addstream"),t.streamList.concat(r),t.onStreamUpdated(O.added,x(r)));t.logger.debug("hasl.0 call success")}(t,e.body.stream_info);break;case q.deleted:!function(t,e){t.logger.debug("hdsl.0 call");for(var i=[],r=0;r<e.length;r++)for(var s=t.streamList.length-1;s>=0;s--)if(e[r].stream_id===t.streamList[s].stream_id){t.streamList.splice(s,1),i.push(e[r]);break}0!==i.length&&(t.logger.debug("hdsl.0 callback delstream"),t.onStreamUpdated(O.deleted,x(i)));t.logger.debug("hdsl.0 call")}(t,e.body.stream_info);break;case q.updated:!function(t,e){t.logger.debug("husl.0 call");for(var i=[],r=0;r<e.length;r++)for(var s=0;s<t.streamList.length;s++)if(e[r].stream_id===t.streamList[s].stream_id){e[r].extra_info!==t.streamList[s].extra_info&&(t.streamList[s]=e[r],i.push(e[r]));break}0!==i.length&&(t.logger.debug("husl.0 callback updatestream"),t.onStreamExtraInfoUpdated(x(i)));t.logger.debug("husl.0 call success")}(t,e.body.stream_info)}t.logger.debug("hpsum.0 call success")}(t,i);break;case"push_kickout":!function(t,e){t.logger.info("hpk.0 call"),g(t,M.logout),S(t);var i={code:W.KICK_OUT.code,msg:W.KICK_OUT.msg+e.body.reason};t.onKickOut(i),t.logger.debug("hpk.0 call success")}(t,i);break;case"stream_url":!function(t,e){if(t.logger.debug("hfsur.0 call"),t.streamQuerying=!1,0!==e.body.err_code)return void t.logger.info("hfsur.0 server error=",e.body.err_code);if(e.body.stream_url_infos&&e.body.stream_url_infos.length>0){var i=e.body.stream_url_infos[0].stream_id,r=e.body.stream_url_infos[0].urls_ws;if(t.streamList.length>0)for(var s=0;s<t.streamList.length;s++){var o=t.mapStreamQuality[i];if(t.streamList[s].stream_id===i){t.streamList[s].urls_ws=r,t.streamList[s].quality=o;break}t.streamList.push({stream_id:i,urls_ws:r,quality:o}),o&&(t.logger.debug("hfsur.0 remove quality"),delete t.mapStreamQuality[i])}else t.streamList.push({stream_id:i,urls_ws:r});var n=t.mapStreamDom[i];n&&(t.logger.debug("hfsur.0 play"),delete t.mapStreamDom[i],d(t,i,n))}}(t,i);break;case"im_chat":!function(t,e){t.logger.debug("hsrmr.0 call");var i,r=t.sendDataMap[e.header.seq];null!=r?("im_chat"!=(i=r._data).data.header.cmd?t.logger.error("hsrmr.0 cmd wrong"+i.data.header.cmd):0===e.body.err_code?i.success&&i.success(e.header.seq,e.body.msg_id,i.data.body.msg_category,i.data.body.msg_type,i.data.body.msg_content):i.error&&i.error(c(e.body.err_code),e.header.seq,i.data.body.msg_category,i.data.body.msg_type,i.data.body.msg_content),delete t.sendDataMap[e.header.seq],t.sendDataList.remove(r)):t.logger.error("hsrmr.0 no found seq="+e.header.seq);t.logger.debug("hsrmr.0 call success")}(t,i);break;case"push_im_chat":!function(t,e){t.onRecvRoomMsg(e.body.chat_data,e.body.server_msg_id,e.body.ret_msg_id)}(t,i);break;case"push_userlist_update":!function(t,e){if(t.logger.debug("hpus.0 call server user seq "+e.body.user_list_seq+" userSeq "+t.userSeq),t.runState!=M.login)return void t.logger.info("hpus.0 not login");if(!t.userStateUpdate)return void t.logger.info("hpus.0 no userStateUpdate flag");if(t.userSeq+e.body.user_actions.length!==e.body.user_list_seq)return t.logger.info("hpus.0 fetch new userlist "+t.userSeq,NaN+e.body.user_list_seq),void A(t);t.userSeq=e.body.user_list_seq,t.logger.debug("hpus.0 push userSeq "+t.userSeq);for(var i=[],r=0;r<e.body.user_actions.length;r++){var s={action:e.body.user_actions[r].Action,idName:e.body.user_actions[r].IdName,nickName:e.body.user_actions[r].NickName,role:e.body.user_actions[r].Role,loginTime:e.body.user_actions[r].LoginTime};i.push(s)}t.onUserStateUpdate(e.body.room_id,i),t.logger.debug("hpus.0 call success")}(t,i);break;case"user_list":!function(t,e){if(t.logger.debug("hfulr.0 call"),0!=e.body.err_code)return t.userQuerying=!1,void t.logger.info("hfulr.0 fetch error "+e.body.err_code);if(!t.userStateUpdate)return;t.userTempList.push.apply(t.userTempList,e.body.user_baseinfos);var i=e.body.ret_user_index,r=e.body.server_user_index;if(i!=r)return t.logger.info("hfulr.0 fetch another page"),void k(i+1);t.userSeq=e.body.server_user_seq,t.logger.info("hfulr.0 set user Seq "+t.userSeq);for(var s=[],o=0;o<t.userTempList.length;o++){var n={idName:t.userTempList[o].id_name,nickName:t.userTempList[o].nick_name,role:t.userTempList[o].role};s.push(n)}t.userQuerying=!1,t.onGetTotalUserList(t.roomid,s),t.userTempList=[],t.logger.debug("hfulr.0 call success user_list "+s+" count "+s.length)}(t,i)}else t.logger.info("ws.bwsh.0 check session fail.");else!function(t,e){if(t.logger.debug("hlr.0 call"),t.runState!==M.trylogin)return void t.logger.info("hlr.0 state error");if(e.header.seq!==t.cmdSeq)return void t.logger.info("hlr.0 in wrong seq, local=",t.cmdSeq,",recv=",e.header.seq);if(0!==e.body.err_code)return function(t,e){if(t.logger.debug("hlf.0 call"),function(t){switch(e.body.err_code){case 1002:case 1003:return!0;default:return!1}}())return void t.logger.info("hlf.0 KeepTry true");var i=t.lastRunState;g(t,M.logout),S(t);var r=c(e.body.err_code);i==M.login?(t.logger.info("hlf.0 callback disconnect"),t.onDisconnect(r)):(t.logger.info("hlf.0 callback error"),u(t,"login")(r));t.logger.debug("hlf.0 call success")}(t,e),void t.logger.info("hlr.0 server error=",e.body.err_code);(function(t,e){t.logger.debug("hls.0 call");var i=t.lastRunState;g(t,M.login),t.userid=e.body.user_id,t.sessionid=e.body.session_id,t.logger.setSessionInfo(t.appid,t.roomid,t.userid,t.idName,t.sessionid),void 0!=e.body.config_info&&(t.logger.setRemoteLogLevel(e.body.config_info.log_level),""!=e.body.config_info.log_url&&t.logger.openLogServer(e.body.config_info.log_url));w(t),v(t),t.heartbeatInterval=e.body.hearbeat_interval,t.heartbeatInterval<Y&&(t.heartbeatInterval=Y);if(t.heartbeatTimer=setTimeout(function(){T(t)},t.heartbeatInterval),y(t),t.sendDataCheckTimer=setTimeout(function(){b(t)},t.sendDataCheckInterval),t.streamQuerying=!1,i==M.login)t.logger.info("hls.0 recover from disconnect so call streamupdate"),R(t,e.body.stream_seq,e.body.stream_info||[]);else{t.logger.info("hls.0 success callback user"),t.streamList=e.body.stream_info||[],t.streamSeq=e.body.stream_seq;var r=[];r=x(t.streamList),function(t,e){return t.callbackList[e+"SuccessCallback"]}(t,"login")(r)}t.logger.debug("hls.0 userStateUpdate "+t.userStateUpdate),t.userStateUpdate&&(t.logger.info("hls.0 fetch all new userlist"),A(t));t.logger.debug("hls.0 call success")})(t,e),t.logger.info("hlr.0 call success.")}(t,i)},t.websocket.onclose=function(e){t.logger.info("ws.oc.0 msg="+JSON.stringify(e)),t.runState!==M.logout?t.runState===M.trylogin&&t.tryLoginCount<=z?t.logger.info("ws.oc.0 is called because of try login"):t.runState===M.login?(t.logger.info("ws.oc.0 is called because of network broken, try again"),g(t,M.trylogin),w(t),_(t)):(t.logger.info("ws.oc.0 out of think!!!"),g(t,M.logout),S(t),t.onDisconnect(W.UNKNOWN)):t.logger.info("ws.oc.0 onclose logout flow call websocket.close")},t.websocket.onerror=function(e){t.logger.info("ws.oe.0 msg="+JSON.stringify(e))}}(t),t.logger.info("tl.0 websocket.onpen send login");var e=C(t);f(t,"login",e),t.logger.debug("tl.0 websocket.onpen call success")}}catch(e){t.logger.error("tl.0 websocket err:"+e)}}t.tryLoginTimer=setTimeout(function(){_(t)},V[t.tryLoginCount%z]),t.logger.debug("tl.0 call success")}else t.logger.info("tl.0 state error")}function C(t){return{id_name:t.idName,nick_name:t.nickName,role:t.role,token:t.token,version:Q,user_state_flag:t.userStateUpdate?1:0}}function S(t){if(t.logger.debug("rr.0 call"),w(t),v(t),y(t),t.streamList=[],t.streamQuerying=!1,t.mapStreamDom={},t.mapStreamQuality={},t.preferPlaySourceType=B.cdn,t.logger.debug("rr.0 call send logout=",t.sessionid),"0"!==t.sessionid){f(t,"logout",{reserve:0})}t.websocket&&(t.websocket.onclose=null,t.websocket.onerror=null,t.websocket.close(),t.websocket=null),t.userid="0",t.sessionid="0",t.logger.setSessionInfo(t.appid,t.roomid,t.userid,t.idName,t.sessionid),t.logger.debug("rr.0 call success")}function E(t){if(t.logger.debug("fsl.0 call"),t.runState===M.login)if(t.streamQuerying)t.logger.info("fsl.0 already doing");else{t.streamQuerying=!0,t.logger.debug("fsl.0 send fetch request");f(t,"stream_info",{reserve:0}),t.logger.debug("fsl.0 call success")}else t.logger.info("fsl.0 state error")}function A(t){t.logger.debug("ful.0 call"),t.userQuerying?t.logger.info("ful.0 is already querying"):(t.userQuerying=!0,t.userTempList=[],k(t,0),t.logger.debug("ful.0 the first time call"))}function k(t,e){t.logger.debug("fulwp.0 call");f(t,"user_list",{user_index:e,sort_type:0}),t.logger.debug("fulwp.0 call success")}function R(t,e,i){t.logger.debug("hfus.0 call"),t.streamSeq=e,function(t,e,i,r){t.logger.debug("msl.0 call");for(var s,o=[],n=[],a=[],h=0;h<i.length;h++){s=!1;for(var l=0;l<e.length;l++)if(i[h].stream_id===e[l].stream_id){i[h].extra_info!==e[l].extra_info&&a.push(i[h]),s=!0;break}s||o.push(i[h])}for(var d=0;d<e.length;d++){s=!1;for(var u=0;u<i.length;u++)if(e[d].stream_id===i[u].stream_id){s=!0;break}s||n.push(e[d])}e=i,r(o,n,a),t.logger.debug("msl.0 call success")}(t,t.streamList,i,function(e,i,r){0!==e.length&&(t.logger.debug("hfus.0 callback addstream"),t.onStreamUpdated(O.added,x(e))),0!==i.length&&(t.logger.debug("hfus.0 callback delstream"),t.onStreamUpdated(O.deleted,x(i))),0!==r.length&&(t.logger.debug("hfus.0 callback updatestream"),t.onStreamExtraInfoUpdated(x(r)))}),t.logger.debug("hfus.0 call success")}function x(t){var e=[];if(void 0!=t&&null!=t)for(var i=0;i<t.length;i++)e.push({anchor_id_name:t[i].anchor_id_name,stream_gid:t[i].stream_gid,anchor_nick_name:t[i].anchor_nick_name,extra_info:t[i].extra_info,stream_id:t[i].stream_id});return e}t.prototype={id:function(t){if(null===t||void 0===t)return this._id;if("number"!=typeof t)throw new Error("Id must be an integer.");this._id=t},data:function(t){if(null===t||void 0===t)return this._data;this._data=t},hasNext:function(){return null!==this.next&&null!==this.next.id()},hasPrev:function(){return null!==this.prev&&null!==this.prev.id()}},e.prototype={insertBefore:function(e,i){var r=new t(this._idCounter,i);return r.next=e,r.prev=e.prev,e.prev.next=r,e.prev=r,++this._idCounter,++this._numNodes,r},addLast:function(t){return this.insertBefore(this.end,t)},add:function(t){return this.addLast(t)},getFirst:function(){return 0===this._numNodes?null:this.start.next},getLast:function(){return 0===this._numNodes?null:this.end.prev},size:function(){return this._numNodes},getFromFirst:function(t){var e=0,i=this.start.next;if(t>=0)for(;e<t&&null!==i;)i=i.next,++e;else i=null;if(null===i)throw"Index out of bounds.";return i},get:function(t){return 0===t?this.getFirst():t===this._numNodes-1?this.getLast():this.getFromFirst(t)},remove:function(t){return t.prev.next=t.next,t.next.prev=t.prev,--this._numNodes,t},removeFirst:function(){var t=null;return this._numNodes>0&&(t=this.remove(this.start.next)),t},removeLast:function(){var t=null;return this._numNodes>0&&(t=this.remove(this.end.prev)),t},removeAll:function(){this.start.next=this.end,this.end.prev=this.start,this._numNodes=0,this._idCounter=0},each:function(t){for(var e=this.start;e.hasNext();)t(e=e.next)},find:function(t){for(var e=this.start,i=!1,r=null;e.hasNext()&&!i;)t(e=e.next)&&(r=e,i=!0);return r},map:function(t){for(var e=this.start,i=[];e.hasNext();)t(e=e.next)&&i.push(e);return i},push:function(t){return this.addLast(t)},unshift:function(t){this._numNodes>0?this.insertBefore(this.start.next,t):this.insertBefore(this.end,t)},pop:function(){return this.removeLast()},shift:function(){return this.removeFirst()}};var L={debug:0,info:1,warn:2,error:3,report:99,disable:100};i.prototype.setLogLevel=function(t){this.logLevel=t,(this.logLevel<L.debug||this.logLevel>L.report)&&(this.logLevel=L.disable)},i.prototype.setRemoteLogLevel=function(t){this.logRemoteLevel=t,(this.logRemoteLevel<L.debug||this.logRemoteLevel>L.report)&&(this.logRemoteLevel=L.disable)},i.prototype.setSessionInfo=function(t,e,i,r,s){this.appid=t,this.roomid=e,this.sessionid=i,this.userid=r,this.userName=s},i.prototype.openLogServer=function(t){if(this.url!=t){if(this.url=t,this.stopLogServer(),!t)return;this.websocket=new WebSocket(t),this.websocket.onopen=function(t){},this.websocket.onclose=function(t){},this.websocket.onmessage=function(t){},this.websocket.onerror=function(t){console.log("ws发生错误！")}}},i.prototype.stopLogServer=function(){this.websocket&&(this.websocket.onclose=null,this.websocket.onerror=null,this.websocket.close(),this.websocket=null)},i.prototype.RemoteLog=function(t,e){if(""!=this.url)if(null==this.websocket||2==this.websocket.readyState||3==this.websocket.readyState){var i=this.url;this.url="",this.openLogServer(i),this.logCacheSend.length<this.logCacheMax&&this.logCacheSend.push(e)}else if(0==this.websocket.readyState)this.logCacheSend.length<this.logCacheMax&&this.logCacheSend.push(e);else if(1==this.websocket.readyState){if(this.logCacheSend>0){for(var r="",s=0;s<this.logCacheSend.length;s++)r=r+this.logCacheSend[s]+"\n";e=r+e,this.logCacheSend=[]}this.websocket.send(e)}else this.logCacheSend.length<this.logCacheMax&&this.logCacheSend.push(e)},i.prototype.log=function(t,e){if(this.logLevel!==L.disable&&this.logLevel<=t)for(this.logCache.push(e);this.logCache.length>this.logCacheMax;)this.logCache.shift();this.logRemoteLevel!==L.disable&&this.logRemoteLevel<=t&&this.RemoteLog(t,e)},i.prototype.debug=function(){var t=r(this,"debug","".concat([].slice.call(arguments)));this.logLevel!==L.disable&&this.logLevel<=L.debug&&console.debug.apply(console,t),this.log(L.debug,t)},i.prototype.info=function(){var t=r(this,"info","".concat([].slice.call(arguments)));this.logLevel!==L.disable&&this.logLevel<=L.info&&console.info.apply(console,t),this.log(L.info,t)},i.prototype.warn=function(){var t=r(this,"warn","".concat([].slice.call(arguments)));this.logLevel!==L.disable&&this.logLevel<=L.warn&&console.warn.apply(console,t),this.log(L.warn,t)},i.prototype.error=function(){var t=r(this,"error","".concat([].slice.call(arguments)));this.logLevel!==L.disable&&this.logLevel<=L.error&&console.error.apply(console,t),this.log(L.error,t)},i.prototype.report=function(){var t=r(this,"report","".concat([].slice.call(arguments)));this.logLevel!==L.disable&&this.logLevel<=L.report&&console.info.apply(console,t),this.log(L.report,t)};var D=["00","01","02","03","04","05","06","07","08","09"],P={Player:null,VideoElement:null,BitBuffer:null,Source:{},Demuxer:{},Decoder:{},Renderer:{},AudioOutput:{},Now:function(){return window.performance?window.performance.now()/1e3:Date.now()/1e3},CreateVideoElements:function(){for(var t=document.querySelectorAll(".jsmpeg"),e=0;e<t.length;e++)new P.VideoElement(t[e])},Fill:function(t,e){if(t.fill)t.fill(e);else for(var i=0;i<t.length;i++)t[i]=e},RendererPool:[],GetRenderer:function(t){var e=null;return this.RendererPool.forEach(function(i){i&&i.key==t&&null!=i.value&&(e=i.value)}),e},SetRenderer:function(t,e){var i={key:t,value:e};this.RendererPool.push(i)},DestroyAllRenderer:function(){console.log("this.RendererPool = ",this.RendererPool),this.RendererPool=[]},DestroySingleRenderer:function(t){console.log("this.RendererPool1 = ",this.RendererPool),console.log("this.key = ",t);for(var e=this.RendererPool.length-1;e>=0;e--)this.RendererPool[e].key==t&&this.RendererPool.splice(e,1);console.log("this.RendererPool2 = ",this.RendererPool)}};"complete"===document.readyState?P.CreateVideoElements():document.addEventListener("DOMContentLoaded",P.CreateVideoElements),P.VideoElement=function(){var t=function(e){var i=e.dataset.url;if(!i)throw"VideoElement has no `data-url` attribute";var r=function(t,e){for(var i in e)t.style[i]=e[i]};this.container=e,r(this.container,{display:"inline-block",position:"relative",minWidth:"80px",minHeight:"80px"}),this.canvas=document.createElement("canvas"),this.canvas.width=960,this.canvas.height=540,r(this.canvas,{display:"block",width:"100%"}),this.container.appendChild(this.canvas),this.playButton=document.createElement("div"),this.playButton.innerHTML=t.PLAY_BUTTON,r(this.playButton,{zIndex:2,position:"absolute",top:"0",bottom:"0",left:"0",right:"0",maxWidth:"75px",maxHeight:"75px",margin:"auto",opacity:"0.7",cursor:"pointer"}),this.container.appendChild(this.playButton);var s={canvas:this.canvas};for(var o in e.dataset)try{s[o]=JSON.parse(e.dataset[o])}catch(t){s[o]=e.dataset[o]}if(this.player=new P.Player(i,s),e.playerInstance=this.player,!s.poster||s.autoplay||this.player.options.streaming||(s.decodeFirstFrame=!1,this.poster=new Image,this.poster.src=s.poster,this.poster.addEventListener("load",this.posterLoaded),r(this.poster,{display:"block",zIndex:1,position:"absolute",top:0,left:0,bottom:0,right:0}),this.container.appendChild(this.poster)),this.player.options.streaming||this.container.addEventListener("click",this.onClick.bind(this)),(s.autoplay||this.player.options.streaming)&&(this.playButton.style.display="none"),this.player.audioOut&&!this.player.audioOut.unlocked){var n=this.container;(s.autoplay||this.player.options.streaming)&&(this.unmuteButton=document.createElement("div"),this.unmuteButton.innerHTML=t.UNMUTE_BUTTON,r(this.unmuteButton,{zIndex:2,position:"absolute",bottom:"10px",right:"20px",width:"75px",height:"75px",margin:"auto",opacity:"0.7",cursor:"pointer"}),this.container.appendChild(this.unmuteButton),n=this.unmuteButton),this.unlockAudioBound=this.onUnlockAudio.bind(this,n),n.addEventListener("touchstart",this.unlockAudioBound,!1),n.addEventListener("click",this.unlockAudioBound,!0)}};return t.prototype.onUnlockAudio=function(t,e){this.unmuteButton&&(e.preventDefault(),e.stopPropagation()),this.player.audioOut.unlock(function(){this.unmuteButton&&(this.unmuteButton.style.display="none"),t.removeEventListener("touchstart",this.unlockAudioBound),t.removeEventListener("click",this.unlockAudioBound)}.bind(this))},t.prototype.onClick=function(t){this.player.isPlaying?(this.player.pause(),this.playButton.style.display="block"):(this.player.play(),this.playButton.style.display="none",this.poster&&(this.poster.style.display="none"))},t.PLAY_BUTTON='<svg style="max-width: 75px; max-height: 75px;" viewBox="0 0 200 200" alt="Play video"><circle cx="100" cy="100" r="90" fill="none" stroke-width="15" stroke="#fff"/><polygon points="70, 55 70, 145 145, 100" fill="#fff"/></svg>',t.UNMUTE_BUTTON='<svg style="max-width: 75px; max-height: 75px;" viewBox="0 0 75 75"><polygon class="audio-speaker" stroke="none" fill="#fff" points="39,13 22,28 6,28 6,47 21,47 39,62 39,13"/><g stroke="#fff" stroke-width="5"><path d="M 49,50 69,26"/><path d="M 69,50 49,26"/></g></svg>',t}(),P.Player=function(){var t=function(t,e){this.options=e||{},e.source?(this.source=new e.source(t,e),e.streaming=!!this.source.streaming):t.match(/^wss?:\/\//)?(this.source=new P.Source.WebSocket(t,e),e.streaming=!0):!1!==e.progressive?(this.source=new P.Source.AjaxProgressive(t,e),e.streaming=!1):(this.source=new P.Source.Ajax(t,e),e.streaming=!1),this.maxAudioLag=e.maxAudioLag||.25,this.loop=!1!==e.loop,this.autoplay=!!e.autoplay||e.streaming,this.demuxer=new P.Demuxer.TS(e),this.source.connect(this.demuxer),!1!==e.video&&("undefined"!=typeof Worker&&e.enableWorker?(this.useWorker=!0,this.video=new P.Decoder.Proxy(e)):(this.useWorker=!1,this.video=new P.Decoder.MPEG1Video(e)),e.canvas&&(this.renderer=P.GetRenderer(e.canvas.id)),null==this.renderer&&(this.isSupported=P.Renderer.WebGL.IsSupported(),this.renderer=!e.disableGl&&this.isSupported?new P.Renderer.WebGL(e):new P.Renderer.Canvas2D(e),P.SetRenderer(e.canvas.id,this.renderer)),this.demuxer.connect(P.Demuxer.TS.STREAM.VIDEO_1,this.video),this.video.connect(this.renderer)),!1!==e.audio&&P.AudioOutput.WebAudio.IsSupported()&&(this.audio=new P.Decoder.MP2Audio(e),this.audioOut=new P.AudioOutput.WebAudio(e),this.demuxer.connect(P.Demuxer.TS.STREAM.AUDIO_1,this.audio),this.audio.connect(this.audioOut)),Object.defineProperty(this,"currentTime",{get:this.getCurrentTime,set:this.setCurrentTime}),Object.defineProperty(this,"volume",{get:this.getVolume,set:this.setVolume}),Object.defineProperty(this,"playoutStatus",{get:this.getPlayoutStatus}),this.lastDecodeTime=0,this.videoBreakCnt=0,this.animationDuration=0,this.unpauseOnShow=!1,!1!==e.pauseWhenHidden&&(this.showHandle=this.showHide.bind(this),document.addEventListener("visibilitychange",this.showHandle,!1)),this.source.start(),this.autoplay&&this.play()};return t.prototype.showHide=function(t){"hidden"===document.visibilityState?(this.unpauseOnShow=this.wantsToPlay,this.pause()):this.unpauseOnShow&&this.play()},t.prototype.play=function(t){this.animationId=requestAnimationFrame(this.update.bind(this)),this.wantsToPlay=!0},t.prototype.pause=function(t){cancelAnimationFrame(this.animationId),this.wantsToPlay=!1,this.isPlaying=!1,this.audio&&this.audio.canPlay&&(this.audioOut.stop(),this.seek(this.currentTime))},t.prototype.getVolume=function(){return this.audioOut?this.audioOut.volume:0},t.prototype.setVolume=function(t){this.audioOut&&(this.audioOut.volume=t)},t.prototype.stop=function(t){this.pause(),this.seek(0),this.video&&!1!==this.options.decodeFirstFrame&&this.video.decode()},t.prototype.destroy=function(){this.pause(),this.source.destroy(),this.source=null,this.demuxer.destroy(),this.demuxer=null,this.video&&(this.video.destroy(),this.video=null),this.renderer&&(this.renderer=null),this.audio&&(this.audio.destroy(),this.audio=null),this.audioOut&&(this.audioOut.destroy(),this.audioOut=null),this.options=null,this.showHandle&&(document.removeEventListener("visibilitychange",this.showHandle),this.showHandle=null)},t.prototype.seek=function(t){var e=this.audio&&this.audio.canPlay?this.audio.startTime:this.video.startTime;this.video&&this.video.seek(t+e),this.audio&&this.audio.seek(t+e),this.startTime=P.Now()-t},t.prototype.getCurrentTime=function(){return this.audio&&this.audio.canPlay?this.audio.currentTime-this.audio.startTime:this.video.currentTime-this.video.startTime},t.prototype.setCurrentTime=function(t){this.seek(t)},t.prototype.update=function(){this.animationId=requestAnimationFrame(this.update.bind(this)),!this.source||this.source.established?(this.isPlaying||(this.isPlaying=!0,this.startTime=P.Now()-this.currentTime),this.options.streaming?this.updateForStreaming():this.updateForStaticFile()):this.renderer&&this.renderer.renderProgress(this.source.progress)},t.prototype.updateForStreaming=function(){var t=P.Now();if(this.video&&(this.video.decode()?(this.lastDecodeTime>0&&(this.animationDuration=t-this.lastDecodeTime),this.lastDecodeTime=t):t-this.lastDecodeTime>500&&this.videoBreakCnt++),this.audio){var e=!1;do{this.audioOut.enqueuedTime>this.maxAudioLag&&(this.audioOut.resetEnqueuedTime(),this.audioOut.enabled=!1),e=this.audio.decode()}while(e);this.audioOut.enabled=!0}},t.prototype.updateForStaticFile=function(){var t=!1,e=0;if(this.audio&&this.audio.canPlay){for(;!t&&this.audio.decodedTime-this.audio.currentTime<.25;)t=!this.audio.decode();this.video&&this.video.currentTime<this.audio.currentTime&&(t=!this.video.decode()),e=this.demuxer.currentTime-this.audio.currentTime}else if(this.video){var i=P.Now()-this.startTime+this.video.startTime,r=i-this.video.currentTime,s=1/this.video.frameRate;this.video&&r>0&&(r>2*s&&(this.startTime+=r),t=!this.video.decode()),e=this.demuxer.currentTime-i}this.source.resume(e),t&&this.source.completed&&(this.loop?this.seek(0):this.pause())},t.prototype.getPlayoutStatus=function(){var t={};return t.videoBytes=this.demuxer.videoBytes,t.videoFrameCnt=this.demuxer.videoFrameCnt,t.videoDecodeFrameCnt=this.video.frameCnt,t.videoBreakCnt=this.videoBreakCnt,t.useWebGL=this.isSupported,t.useWorker=this.useWorker,t.animationDuration=this.animationDuration,t},t.prototype.enableRender=function(t){this.renderer.enable(t)},t.prototype.enableDecode=function(t){this.video.setEnable(t)},t}(),P.BitBuffer=function(){var t=function(e,i){"object"==typeof e?(this.bytes=e instanceof Uint8Array?e:new Uint8Array(e),this.byteLength=this.bytes.length):(this.bytes=new Uint8Array(e||1048576),this.byteLength=0),this.mode=i||t.MODE.EXPAND,this.index=0};return t.prototype.resize=function(t){var e=new Uint8Array(t);0!==this.byteLength&&(this.byteLength=Math.min(this.byteLength,t),e.set(this.bytes,0,this.byteLength)),this.bytes=e,this.index=Math.min(this.index,this.byteLength<<3)},t.prototype.evict=function(t){var e=this.index>>3,i=this.bytes.length-this.byteLength;if(this.index===this.byteLength<<3||t>i+e)return this.byteLength=0,void(this.index=0);0!==e&&(this.bytes.copyWithin?this.bytes.copyWithin(0,e,this.byteLength):this.bytes.set(this.bytes.subarray(e,this.byteLength)),this.byteLength=this.byteLength-e,this.index-=e<<3)},t.prototype.write=function(e){var i="object"==typeof e[0],r=0,s=this.bytes.length-this.byteLength;if(i)for(var r=0,o=0;o<e.length;o++)r+=e[o].byteLength;else r=e.byteLength;if(r>s)if(this.mode===t.MODE.EXPAND){var n=Math.max(2*this.bytes.length,r-s);this.resize(n)}else this.evict(r);if(i)for(o=0;o<e.length;o++)this.appendSingleBuffer(e[o]);else this.appendSingleBuffer(e)},t.prototype.appendSingleBuffer=function(t){t=t instanceof Uint8Array?t:new Uint8Array(t),this.bytes.set(t,this.byteLength),this.byteLength+=t.length},t.prototype.findNextStartCode=function(){for(var t=this.index+7>>3;t<this.byteLength;t++)if(0==this.bytes[t]&&0==this.bytes[t+1]&&1==this.bytes[t+2])return this.index=t+4<<3,this.bytes[t+3];return this.index=this.byteLength<<3,-1},t.prototype.findStartCode=function(t){for(var e=0;;)if((e=this.findNextStartCode())===t||-1===e)return e;return-1},t.prototype.nextBytesAreStartCode=function(){var t=this.index+7>>3;return t>=this.byteLength||0==this.bytes[t]&&0==this.bytes[t+1]&&1==this.bytes[t+2]},t.prototype.peek=function(t){for(var e=this.index,i=0;t;){var r=this.bytes[e>>3],s=8-(7&e),o=s<t?s:t,n=s-o;i=i<<o|(r&255>>8-o<<n)>>n,e+=o,t-=o}return i},t.prototype.read=function(t){var e=this.peek(t);return this.index+=t,e},t.prototype.skip=function(t){return this.index+=t},t.prototype.rewind=function(t){this.index=Math.max(this.index-t,0)},t.prototype.has=function(t){return(this.byteLength<<3)-this.index>=t},t.MODE={EVICT:1,EXPAND:2},t}(),P.Source.Ajax=function(){var t=function(t,e){this.url=t,this.destination=null,this.request=null,this.completed=!1,this.established=!1,this.progress=0};return t.prototype.connect=function(t){this.destination=t},t.prototype.start=function(){this.request=new XMLHttpRequest,this.request.onreadystatechange=function(){this.request.readyState===this.request.DONE&&200===this.request.status&&this.onLoad(this.request.response)}.bind(this),this.request.onprogress=this.onProgress.bind(this),this.request.open("GET",this.url),this.request.responseType="arraybuffer",this.request.send()},t.prototype.resume=function(t){},t.prototype.destroy=function(){this.request.abort()},t.prototype.onProgress=function(t){this.progress=t.loaded/t.total},t.prototype.onLoad=function(t){this.established=!0,this.completed=!0,this.progress=1,this.destination&&this.destination.write(t)},t}(),P.Source.AjaxProgressive=function(){var t=function(t,e){this.url=t,this.destination=null,this.request=null,this.completed=!1,this.established=!1,this.progress=0,this.fileSize=0,this.loadedSize=0,this.chunkSize=e.chunkSize||1048576,this.isLoading=!1,this.loadStartTime=0,this.throttled=!1!==e.throttled,this.aborted=!1};return t.prototype.connect=function(t){this.destination=t},t.prototype.start=function(){this.request=new XMLHttpRequest,this.request.onreadystatechange=function(){this.request.readyState===this.request.DONE&&(this.fileSize=parseInt(this.request.getResponseHeader("Content-Length")),this.loadNextChunk())}.bind(this),this.request.onprogress=this.onProgress.bind(this),this.request.open("HEAD",this.url),this.request.send()},t.prototype.resume=function(t){if(!this.isLoading&&this.throttled){8*this.loadTime+2>t&&this.loadNextChunk()}},t.prototype.destroy=function(){this.request.abort(),this.aborted=!0},t.prototype.loadNextChunk=function(){var t=this.loadedSize,e=Math.min(this.loadedSize+this.chunkSize-1,this.fileSize-1);t>=this.fileSize||this.aborted?this.completed=!0:(this.isLoading=!0,this.loadStartTime=P.Now(),this.request=new XMLHttpRequest,this.request.onreadystatechange=function(){this.request.readyState===this.request.DONE&&this.request.status>=200&&this.request.status<300?this.onChunkLoad(this.request.response):this.request.readyState===this.request.DONE&&this.loadFails++<3&&this.loadNextChunk()}.bind(this),0===t&&(this.request.onprogress=this.onProgress.bind(this)),this.request.open("GET",this.url+"?"+t+"-"+e),this.request.setRequestHeader("Range","bytes="+t+"-"+e),this.request.responseType="arraybuffer",this.request.send())},t.prototype.onProgress=function(t){this.progress=t.loaded/t.total},t.prototype.onChunkLoad=function(t){this.established=!0,this.progress=1,this.loadedSize+=t.byteLength,this.loadFails=0,this.isLoading=!1,this.destination&&this.destination.write(t),this.loadTime=P.Now()-this.loadStartTime,this.throttled||this.loadNextChunk()},t}(),P.Source.WebSocket=function(){var t=function(t,e){this.url=t,this.options=e,this.socket=null,this.callbacks={connect:[],data:[]},this.destination=null,this.reconnectInterval=void 0!==e.reconnectInterval?e.reconnectInterval:5,this.shouldAttemptReconnect=!!this.reconnectInterval,this.completed=!1,this.established=!1,this.progress=0,this.reconnectTimeoutId=0};return t.prototype.connect=function(t){this.destination=t},t.prototype.destroy=function(){clearTimeout(this.reconnectTimeoutId),this.shouldAttemptReconnect=!1,this.socket.close(),this.socket.onmessage=null,this.socket.onopen=null,this.socket.onerror=null,this.socket.onerror=null,this.socket.onclose=null,this.socket=null,this.destination=null,this.options=null,this.url=null},t.prototype.start=function(){this.shouldAttemptReconnect=!!this.reconnectInterval,this.progress=0,this.established=!1,this.socket=new WebSocket(this.url,this.options.protocols||null),this.socket.binaryType="arraybuffer",this.socket.onmessage=this.onMessage.bind(this),this.socket.onopen=this.onOpen.bind(this),this.socket.onerror=this.onClose.bind(this),this.socket.onclose=this.onClose.bind(this)},t.prototype.resume=function(t){},t.prototype.onOpen=function(){this.progress=1,this.established=!0},t.prototype.onClose=function(){this.shouldAttemptReconnect&&(clearTimeout(this.reconnectTimeoutId),this.reconnectTimeoutId=setTimeout(function(){this.start()}.bind(this),1e3*this.reconnectInterval))},t.prototype.onMessage=function(t){this.destination&&this.destination.write(t.data)},t}(),P.Demuxer.TS=function(){var t=function(t){this.bits=null,this.leftoverBytes=null,this.guessVideoFrameEnd=!0,this.pidsToStreamIds={},this.pesPacketInfo={},this.startTime=0,this.currentTime=0,Object.defineProperty(this,"videoBytes",{get:this.getVideoBytes}),Object.defineProperty(this,"videoFrameCnt",{get:this.getVideoFrameCnt}),Object.defineProperty(this,"audioBytes",{get:this.getAudioBytes}),Object.defineProperty(this,"audioFrameCnt",{get:this.getAudioFrameCnt})};return t.prototype.connect=function(t,e){this.pesPacketInfo[t]={destination:e,currentLength:0,totalLength:0,pts:0,buffers:[],bytes:0,frameCnt:0}},t.prototype.destroy=function(){var e=this.pesPacketInfo[t.STREAM.VIDEO_1];void 0!==e.destination&&(e.destination=null),void 0!==(e=this.pesPacketInfo[t.STREAM.AUDIO_1]).destination&&(e.destination=null)},t.prototype.write=function(t){if(this.leftoverBytes){var e=t.byteLength+this.leftoverBytes.byteLength;this.bits=new P.BitBuffer(e),this.bits.write([this.leftoverBytes,t])}else this.bits=new P.BitBuffer(t);for(;this.bits.has(1504)&&this.parsePacket(););var i=this.bits.byteLength-(this.bits.index>>3);this.leftoverBytes=i>0?this.bits.bytes.subarray(this.bits.index>>3):null},t.prototype.parsePacket=function(){if(71!==this.bits.read(8)&&!this.resync())return!1;var t=187+(this.bits.index>>3),e=(this.bits.read(1),this.bits.read(1)),i=(this.bits.read(1),this.bits.read(13)),r=(this.bits.read(2),this.bits.read(2)),s=(this.bits.read(4),this.pidsToStreamIds[i]);if(e&&s){(f=this.pesPacketInfo[s])&&f.currentLength&&this.packetComplete(f)}if(1&r){if(2&r){var o=this.bits.read(8);this.bits.skip(o<<3)}if(e&&this.bits.nextBytesAreStartCode()){this.bits.skip(24),s=this.bits.read(8),this.pidsToStreamIds[i]=s;var n=this.bits.read(16);this.bits.skip(8);var a=this.bits.read(2);this.bits.skip(6);var h=this.bits.read(8),l=this.bits.index+(h<<3);if(f=this.pesPacketInfo[s]){var d=0;if(2&a){this.bits.skip(4);var u=this.bits.read(3);this.bits.skip(1);var c=this.bits.read(15);this.bits.skip(1);var g=this.bits.read(15);this.bits.skip(1),d=(1073741824*u+32768*c+g)/9e4,this.currentTime=d,-1===this.startTime&&(this.startTime=d)}var p=n?n-h-3:0;this.packetStart(f,d,p)}this.bits.index=l}if(s){var f=this.pesPacketInfo[s];if(f){var m=this.bits.index>>3,b=this.packetAddData(f,m,t),y=!e&&2&r;(b||this.guessVideoFrameEnd&&y)&&this.packetComplete(f)}}}return this.bits.index=t<<3,!0},t.prototype.resync=function(){if(!this.bits.has(9024))return!1;for(var t=this.bits.index>>3,e=0;e<187;e++)if(71===this.bits.bytes[t+e]){for(var i=!0,r=1;r<5;r++)if(71!==this.bits.bytes[t+e+188*r]){i=!1;break}if(i)return this.bits.index=t+e+1<<3,!0}return console.warn("JSMpeg: Possible garbage data. Skipping."),this.bits.skip(1496),!1},t.prototype.packetStart=function(t,e,i){t.totalLength=i,t.currentLength=0,t.pts=e},t.prototype.packetAddData=function(t,e,i){t.buffers.push(this.bits.bytes.subarray(e,i)),t.currentLength+=i-e;return 0!==t.totalLength&&t.currentLength>=t.totalLength},t.prototype.packetComplete=function(t){t.destination.write(t.pts,t.buffers),t.bytes=t.bytes+t.currentLength,t.frameCnt++,t.totalLength=0,t.currentLength=0,t.buffers=[]},t.prototype.getAudioBytes=function(){return this.pesPacketInfo[t.STREAM.AUDIO_1].bytes},t.prototype.getAudioFrameCnt=function(){return this.pesPacketInfo[t.STREAM.AUDIO_1].frameCnt},t.prototype.getVideoBytes=function(){return this.pesPacketInfo[t.STREAM.VIDEO_1].bytes},t.prototype.getVideoFrameCnt=function(){return this.pesPacketInfo[t.STREAM.VIDEO_1].frameCnt},t.STREAM={PACK_HEADER:186,SYSTEM_HEADER:187,PROGRAM_MAP:188,PRIVATE_1:189,PADDING:190,PRIVATE_2:191,AUDIO_1:192,VIDEO_1:224,DIRECTORY:255},t}(),P.Decoder.Base=function(){var t=function(t){this.destination=null,this.canPlay=!1,this.collectTimestamps=!t.streaming,this.timestamps=[],this.timestampIndex=0,this.startTime=0,this.decodedTime=0,this.decodedFrameCnt=0,this.decoderResetCnt=0,Object.defineProperty(this,"currentTime",{get:this.getCurrentTime}),Object.defineProperty(this,"frameCnt",{get:this.getDecodedFrameCnt})};return t.prototype.connect=function(t){this.destination=t},t.prototype.destroy=function(){this.destination=null},t.prototype.write=function(t,e){this.collectTimestamps&&(0===this.timestamps.length&&(this.startTime=t,this.decodedTime=t),this.timestamps.push({index:this.bits.byteLength<<3,time:t})),this.bits.write(e),this.canPlay=!0},t.prototype.seek=function(t){if(this.collectTimestamps){this.timestampIndex=0;for(var e=0;e<this.timestamps.length&&!(this.timestamps[e].time>t);e++)this.timestampIndex=e;var i=this.timestamps[this.timestampIndex];i?(this.bits.index=i.index,this.decodedTime=i.time):(this.bits.index=0,this.decodedTime=this.startTime)}},t.prototype.decode=function(){this.advanceDecodedTime(0)},t.prototype.advanceDecodedTime=function(t){if(this.collectTimestamps){for(var e=-1,i=this.timestampIndex;i<this.timestamps.length&&!(this.timestamps[i].index>this.bits.index);i++)e=i;if(-1!==e&&e!==this.timestampIndex)return this.timestampIndex=e,void(this.decodedTime=this.timestamps[this.timestampIndex].time)}this.decodedTime+=t},t.prototype.getCurrentTime=function(){return this.decodedTime},t.prototype.getDecodedFrameCnt=function(){return this.decodedFrameCnt},t}(),P.Decoder.MPEG1Video=function(){var t=function(t){P.Decoder.Base.call(this,t);var e=t.videoBufferSize||524288,i=t.streaming?P.BitBuffer.MODE.EVICT:P.BitBuffer.MODE.EXPAND;this.bits=new P.BitBuffer(e,i),this.customIntraQuantMatrix=new Uint8Array(64),this.customNonIntraQuantMatrix=new Uint8Array(64),this.blockData=new Int32Array(64),this.currentFrame=0,this.decodeFirstFrame=!1!==t.decodeFirstFrame,this._resetCnt=0,Object.defineProperty(this,"resetCnt",{get:this.getResetCnt}),this.enabled=!0};return t.prototype=Object.create(P.Decoder.Base.prototype),t.prototype.constructor=t,t.prototype.write=function(e,i){if(this.enabled&&(P.Decoder.Base.prototype.write.call(this,e,i),!this.hasSequenceHeader)){if(-1===this.bits.findStartCode(t.START.SEQUENCE))return!1;this.decodeSequenceHeader(),this.decodeFirstFrame&&this.decode()}},t.prototype.decode=function(){if(!this.hasSequenceHeader)return!1;if(-1===this.bits.findStartCode(t.START.PICTURE)){this.bits.byteLength,this.bits.index;return!1}return this.decodePicture(),this.advanceDecodedTime(1/this.frameRate),!0},t.prototype.readHuffman=function(t){var e=0;do{e=t[e+this.bits.read(1)]}while(e>=0&&0!==t[e]);return t[e+2]},t.prototype.getResetCnt=function(){return this._resetCnt},t.prototype.setEnable=function(t){this.enabled=t,t||(this.hasSequenceHeader=!1)},t.prototype.frameRate=30,t.prototype.decodeSequenceHeader=function(){var e=this.bits.read(12),i=this.bits.read(12);if(this.bits.skip(4),this.frameRate=t.PICTURE_RATE[this.bits.read(4)],this.bits.skip(30),e===this.width&&i===this.height||(this.width=e,this.height=i,this.initBuffers(),this.destination&&(this.resizeCnt++,this.destination.resize(e,i))),this.bits.read(1)){for(r=0;r<64;r++)this.customIntraQuantMatrix[t.ZIG_ZAG[r]]=this.bits.read(8);this.intraQuantMatrix=this.customIntraQuantMatrix}if(this.bits.read(1)){for(var r=0;r<64;r++){var s=t.ZIG_ZAG[r];this.customNonIntraQuantMatrix[s]=this.bits.read(8)}this.nonIntraQuantMatrix=this.customNonIntraQuantMatrix}this.hasSequenceHeader=!0},t.prototype.initBuffers=function(){this.intraQuantMatrix=t.DEFAULT_INTRA_QUANT_MATRIX,this.nonIntraQuantMatrix=t.DEFAULT_NON_INTRA_QUANT_MATRIX,this.mbWidth=this.width+15>>4,this.mbHeight=this.height+15>>4,this.mbSize=this.mbWidth*this.mbHeight,this.codedWidth=this.mbWidth<<4,this.codedHeight=this.mbHeight<<4,this.codedSize=this.codedWidth*this.codedHeight,this.halfWidth=this.mbWidth<<3,this.halfHeight=this.mbHeight<<3,this.currentY=new Uint8ClampedArray(this.codedSize),this.currentY32=new Uint32Array(this.currentY.buffer),this.currentCr=new Uint8ClampedArray(this.codedSize>>2),this.currentCr32=new Uint32Array(this.currentCr.buffer),this.currentCb=new Uint8ClampedArray(this.codedSize>>2),this.currentCb32=new Uint32Array(this.currentCb.buffer),this.forwardY=new Uint8ClampedArray(this.codedSize),this.forwardY32=new Uint32Array(this.forwardY.buffer),this.forwardCr=new Uint8ClampedArray(this.codedSize>>2),this.forwardCr32=new Uint32Array(this.forwardCr.buffer),this.forwardCb=new Uint8ClampedArray(this.codedSize>>2),this.forwardCb32=new Uint32Array(this.forwardCb.buffer)},t.prototype.currentY=null,t.prototype.currentCr=null,t.prototype.currentCb=null,t.prototype.pictureType=0,t.prototype.forwardY=null,t.prototype.forwardCr=null,t.prototype.forwardCb=null,t.prototype.fullPelForward=!1,t.prototype.forwardFCode=0,t.prototype.forwardRSize=0,t.prototype.forwardF=0,t.prototype.decodePicture=function(e){if(this.currentFrame++,this.bits.skip(10),this.pictureType=this.bits.read(3),this.bits.skip(16),!(this.pictureType<=0||this.pictureType>=t.PICTURE_TYPE.B)){if(this.pictureType===t.PICTURE_TYPE.PREDICTIVE){if(this.fullPelForward=this.bits.read(1),this.forwardFCode=this.bits.read(3),0===this.forwardFCode)return;this.forwardRSize=this.forwardFCode-1,this.forwardF=1<<this.forwardRSize}var i=0;do{i=this.bits.findNextStartCode()}while(i===t.START.EXTENSION||i===t.START.USER_DATA);for(;i>=t.START.SLICE_FIRST&&i<=t.START.SLICE_LAST;)this.decodeSlice(255&i),i=this.bits.findNextStartCode();if(-1!==i&&this.bits.rewind(32),this.decodedFrameCnt++,this.destination&&this.destination.render(this.currentY,this.currentCr,this.currentCb),this.pictureType===t.PICTURE_TYPE.INTRA||this.pictureType===t.PICTURE_TYPE.PREDICTIVE){var r=this.forwardY,s=this.forwardY32,o=this.forwardCr,n=this.forwardCr32,a=this.forwardCb,h=this.forwardCb32;this.forwardY=this.currentY,this.forwardY32=this.currentY32,this.forwardCr=this.currentCr,this.forwardCr32=this.currentCr32,this.forwardCb=this.currentCb,this.forwardCb32=this.currentCb32,this.currentY=r,this.currentY32=s,this.currentCr=o,this.currentCr32=n,this.currentCb=a,this.currentCb32=h}}},t.prototype.quantizerScale=0,t.prototype.sliceBegin=!1,t.prototype.decodeSlice=function(t){for(this.sliceBegin=!0,this.macroblockAddress=(t-1)*this.mbWidth-1,this.motionFwH=this.motionFwHPrev=0,this.motionFwV=this.motionFwVPrev=0,this.dcPredictorY=128,this.dcPredictorCr=128,this.dcPredictorCb=128,this.quantizerScale=this.bits.read(5);this.bits.read(1);)this.bits.skip(8);do{this.decodeMacroblock()}while(!this.bits.nextBytesAreStartCode())},t.prototype.macroblockAddress=0,t.prototype.mbRow=0,t.prototype.mbCol=0,t.prototype.macroblockType=0,t.prototype.macroblockIntra=!1,t.prototype.macroblockMotFw=!1,t.prototype.motionFwH=0,t.prototype.motionFwV=0,t.prototype.motionFwHPrev=0,t.prototype.motionFwVPrev=0,t.prototype.decodeMacroblock=function(){for(var e=0,i=this.readHuffman(t.MACROBLOCK_ADDRESS_INCREMENT);34===i;)i=this.readHuffman(t.MACROBLOCK_ADDRESS_INCREMENT);for(;35===i;)e+=33,i=this.readHuffman(t.MACROBLOCK_ADDRESS_INCREMENT);if(e+=i,this.sliceBegin)this.sliceBegin=!1,this.macroblockAddress+=e;else{if(this.macroblockAddress+e>=this.mbSize)return;for(e>1&&(this.dcPredictorY=128,this.dcPredictorCr=128,this.dcPredictorCb=128,this.pictureType===t.PICTURE_TYPE.PREDICTIVE&&(this.motionFwH=this.motionFwHPrev=0,this.motionFwV=this.motionFwVPrev=0));e>1;)this.macroblockAddress++,this.mbRow=this.macroblockAddress/this.mbWidth|0,this.mbCol=this.macroblockAddress%this.mbWidth,this.copyMacroblock(this.motionFwH,this.motionFwV,this.forwardY,this.forwardCr,this.forwardCb),e--;this.macroblockAddress++}this.mbRow=this.macroblockAddress/this.mbWidth|0,this.mbCol=this.macroblockAddress%this.mbWidth;var r=t.MACROBLOCK_TYPE[this.pictureType];this.macroblockType=this.readHuffman(r),this.macroblockIntra=1&this.macroblockType,this.macroblockMotFw=8&this.macroblockType,0!=(16&this.macroblockType)&&(this.quantizerScale=this.bits.read(5)),this.macroblockIntra?(this.motionFwH=this.motionFwHPrev=0,this.motionFwV=this.motionFwVPrev=0):(this.dcPredictorY=128,this.dcPredictorCr=128,this.dcPredictorCb=128,this.decodeMotionVectors(),this.copyMacroblock(this.motionFwH,this.motionFwV,this.forwardY,this.forwardCr,this.forwardCb));for(var s=0!=(2&this.macroblockType)?this.readHuffman(t.CODE_BLOCK_PATTERN):this.macroblockIntra?63:0,o=0,n=32;o<6;o++)0!=(s&n)&&this.decodeBlock(o),n>>=1},t.prototype.decodeMotionVectors=function(){var e,i,r=0;this.macroblockMotFw?(0!==(e=this.readHuffman(t.MOTION))&&1!==this.forwardF?(r=this.bits.read(this.forwardRSize),i=(Math.abs(e)-1<<this.forwardRSize)+r+1,e<0&&(i=-i)):i=e,this.motionFwHPrev+=i,this.motionFwHPrev>(this.forwardF<<4)-1?this.motionFwHPrev-=this.forwardF<<5:this.motionFwHPrev<-this.forwardF<<4&&(this.motionFwHPrev+=this.forwardF<<5),this.motionFwH=this.motionFwHPrev,this.fullPelForward&&(this.motionFwH<<=1),0!==(e=this.readHuffman(t.MOTION))&&1!==this.forwardF?(r=this.bits.read(this.forwardRSize),i=(Math.abs(e)-1<<this.forwardRSize)+r+1,e<0&&(i=-i)):i=e,this.motionFwVPrev+=i,this.motionFwVPrev>(this.forwardF<<4)-1?this.motionFwVPrev-=this.forwardF<<5:this.motionFwVPrev<-this.forwardF<<4&&(this.motionFwVPrev+=this.forwardF<<5),this.motionFwV=this.motionFwVPrev,this.fullPelForward&&(this.motionFwV<<=1)):this.pictureType===t.PICTURE_TYPE.PREDICTIVE&&(this.motionFwH=this.motionFwHPrev=0,this.motionFwV=this.motionFwVPrev=0)},t.prototype.copyMacroblock=function(t,e,i,r,s){var o,n,a,h,l,d,u,c,g,p=this.currentY32,f=this.currentCb32,m=this.currentCr32;n=(o=this.codedWidth)-16,a=t>>1,h=e>>1,l=1==(1&t),d=1==(1&e),u=((this.mbRow<<4)+h)*o+(this.mbCol<<4)+a,g=(c=this.mbRow*o+this.mbCol<<2)+(o<<2);var b,y,v,T;if(l)if(d)for(;c<g;){for(y=i[u]+i[u+o],u++,b=0;b<4;b++)T=y+(v=i[u]+i[u+o])+2>>2&255,T|=(y=i[++u]+i[u+o])+v+2<<6&65280,T|=y+(v=i[++u]+i[u+o])+2<<14&16711680,y=i[++u]+i[u+o],u++,T|=y+v+2<<22&4278190080,p[c++]=T;c+=n>>2,u+=n-1}else for(;c<g;){for(y=i[u++],b=0;b<4;b++)T=y+(v=i[u++])+1>>1&255,T|=(y=i[u++])+v+1<<7&65280,T|=y+(v=i[u++])+1<<15&16711680,T|=(y=i[u++])+v+1<<23&4278190080,p[c++]=T;c+=n>>2,u+=n-1}else if(d)for(;c<g;){for(b=0;b<4;b++)T=i[u]+i[u+o]+1>>1&255,T|=i[++u]+i[u+o]+1<<7&65280,T|=i[++u]+i[u+o]+1<<15&16711680,T|=i[++u]+i[u+o]+1<<23&4278190080,u++,p[c++]=T;c+=n>>2,u+=n}else for(;c<g;){for(b=0;b<4;b++)T=i[u],T|=i[++u]<<8,T|=i[++u]<<16,T|=i[++u]<<24,u++,p[c++]=T;c+=n>>2,u+=n}n=(o=this.halfWidth)-8,a=t/2>>1,h=e/2>>1,l=1==(t/2&1),d=1==(e/2&1),u=((this.mbRow<<3)+h)*o+(this.mbCol<<3)+a,g=(c=this.mbRow*o+this.mbCol<<1)+(o<<1);var w,_,C,S,E,A;if(l)if(d)for(;c<g;){for(w=r[u]+r[u+o],S=s[u]+s[u+o],u++,b=0;b<2;b++)C=w+(_=r[u]+r[u+o])+2>>2&255,A=S+(E=s[u]+s[u+o])+2>>2&255,C|=(w=r[++u]+r[u+o])+_+2<<6&65280,A|=(S=s[u]+s[u+o])+E+2<<6&65280,C|=w+(_=r[++u]+r[u+o])+2<<14&16711680,A|=S+(E=s[u]+s[u+o])+2<<14&16711680,w=r[++u]+r[u+o],S=s[u]+s[u+o],u++,C|=w+_+2<<22&4278190080,A|=S+E+2<<22&4278190080,m[c]=C,f[c]=A,c++;c+=n>>2,u+=n-1}else for(;c<g;){for(w=r[u],S=s[u],u++,b=0;b<2;b++)C=w+(_=r[u])+1>>1&255,A=S+(E=s[u++])+1>>1&255,C|=(w=r[u])+_+1<<7&65280,A|=(S=s[u++])+E+1<<7&65280,C|=w+(_=r[u])+1<<15&16711680,A|=S+(E=s[u++])+1<<15&16711680,C|=(w=r[u])+_+1<<23&4278190080,A|=(S=s[u++])+E+1<<23&4278190080,m[c]=C,f[c]=A,c++;c+=n>>2,u+=n-1}else if(d)for(;c<g;){for(b=0;b<2;b++)C=r[u]+r[u+o]+1>>1&255,A=s[u]+s[u+o]+1>>1&255,C|=r[++u]+r[u+o]+1<<7&65280,A|=s[u]+s[u+o]+1<<7&65280,C|=r[++u]+r[u+o]+1<<15&16711680,A|=s[u]+s[u+o]+1<<15&16711680,C|=r[++u]+r[u+o]+1<<23&4278190080,A|=s[u]+s[u+o]+1<<23&4278190080,u++,m[c]=C,f[c]=A,c++;c+=n>>2,u+=n}else for(;c<g;){for(b=0;b<2;b++)C=r[u],A=s[u],C|=r[++u]<<8,A|=s[u]<<8,C|=r[++u]<<16,A|=s[u]<<16,C|=r[++u]<<24,A|=s[u]<<24,u++,m[c]=C,f[c]=A,c++;c+=n>>2,u+=n}},t.prototype.dcPredictorY=0,t.prototype.dcPredictorCr=0,t.prototype.dcPredictorCb=0,t.prototype.blockData=null,t.prototype.decodeBlock=function(e){var i,r=0;if(this.macroblockIntra){var s,o;if(e<4?(s=this.dcPredictorY,o=this.readHuffman(t.DCT_DC_SIZE_LUMINANCE)):(s=4===e?this.dcPredictorCr:this.dcPredictorCb,o=this.readHuffman(t.DCT_DC_SIZE_CHROMINANCE)),o>0){var n=this.bits.read(o);this.blockData[0]=0!=(n&1<<o-1)?s+n:s+(-1<<o|n+1)}else this.blockData[0]=s;e<4?this.dcPredictorY=this.blockData[0]:4===e?this.dcPredictorCr=this.blockData[0]:this.dcPredictorCb=this.blockData[0],this.blockData[0]<<=8,i=this.intraQuantMatrix,r=1}else i=this.nonIntraQuantMatrix;for(var a=0;;){var h=0,l=this.readHuffman(t.DCT_COEFF);if(1===l&&r>0&&0===this.bits.read(1))break;65535===l?(h=this.bits.read(6),0===(a=this.bits.read(8))?a=this.bits.read(8):128===a?a=this.bits.read(8)-256:a>128&&(a-=256)):(h=l>>8,a=255&l,this.bits.read(1)&&(a=-a));var d=t.ZIG_ZAG[r+=h];r++,a<<=1,this.macroblockIntra||(a+=a<0?-1:1),0==(1&(a=a*this.quantizerScale*i[d]>>4))&&(a-=a>0?1:-1),a>2047?a=2047:a<-2048&&(a=-2048),this.blockData[d]=a*t.PREMULTIPLIER_MATRIX[d]}var u,c,g;e<4?(u=this.currentY,g=this.codedWidth-8,c=this.mbRow*this.codedWidth+this.mbCol<<4,0!=(1&e)&&(c+=8),0!=(2&e)&&(c+=this.codedWidth<<3)):(u=4===e?this.currentCb:this.currentCr,g=(this.codedWidth>>1)-8,c=(this.mbRow*this.codedWidth<<2)+(this.mbCol<<3)),this.macroblockIntra?1===r?(t.CopyValueToDestination(this.blockData[0]+128>>8,u,c,g),this.blockData[0]=0):(t.IDCT(this.blockData),t.CopyBlockToDestination(this.blockData,u,c,g),P.Fill(this.blockData,0)):1===r?(t.AddValueToDestination(this.blockData[0]+128>>8,u,c,g),this.blockData[0]=0):(t.IDCT(this.blockData),t.AddBlockToDestination(this.blockData,u,c,g),P.Fill(this.blockData,0)),r=0},t.CopyBlockToDestination=function(t,e,i,r){for(var s=0;s<64;s+=8,i+=r+8)e[i+0]=t[s+0],e[i+1]=t[s+1],e[i+2]=t[s+2],e[i+3]=t[s+3],e[i+4]=t[s+4],e[i+5]=t[s+5],e[i+6]=t[s+6],e[i+7]=t[s+7]},t.AddBlockToDestination=function(t,e,i,r){for(var s=0;s<64;s+=8,i+=r+8)e[i+0]+=t[s+0],e[i+1]+=t[s+1],e[i+2]+=t[s+2],e[i+3]+=t[s+3],e[i+4]+=t[s+4],e[i+5]+=t[s+5],e[i+6]+=t[s+6],e[i+7]+=t[s+7]},t.CopyValueToDestination=function(t,e,i,r){for(var s=0;s<64;s+=8,i+=r+8)e[i+0]=t,e[i+1]=t,e[i+2]=t,e[i+3]=t,e[i+4]=t,e[i+5]=t,e[i+6]=t,e[i+7]=t},t.AddValueToDestination=function(t,e,i,r){for(var s=0;s<64;s+=8,i+=r+8)e[i+0]+=t,e[i+1]+=t,e[i+2]+=t,e[i+3]+=t,e[i+4]+=t,e[i+5]+=t,e[i+6]+=t,e[i+7]+=t},t.IDCT=function(t){for(var e,i,r,s,o,n,a,h,l,d,u,c,g,p,f,m,b,y,v=0;v<8;++v)e=t[32+v],i=t[16+v]+t[48+v],r=t[40+v]-t[24+v],n=t[8+v]+t[56+v],a=t[24+v]+t[40+v],l=(g=(473*(s=t[8+v]-t[56+v])-196*r+128>>8)-(o=n+a))-(362*(n-a)+128>>8),p=(d=(h=t[0+v])-e)+(u=(362*(t[16+v]-t[48+v])+128>>8)-i),f=(c=h+e)+i,m=d-u,b=c-i,y=-l-(473*r+196*s+128>>8),t[0+v]=o+f,t[8+v]=g+p,t[16+v]=m-l,t[24+v]=b-y,t[32+v]=b+y,t[40+v]=l+m,t[48+v]=p-g,t[56+v]=f-o;for(v=0;v<64;v+=8)e=t[4+v],i=t[2+v]+t[6+v],r=t[5+v]-t[3+v],n=t[1+v]+t[7+v],a=t[3+v]+t[5+v],l=(g=(473*(s=t[1+v]-t[7+v])-196*r+128>>8)-(o=n+a))-(362*(n-a)+128>>8),p=(d=(h=t[0+v])-e)+(u=(362*(t[2+v]-t[6+v])+128>>8)-i),f=(c=h+e)+i,m=d-u,b=c-i,y=-l-(473*r+196*s+128>>8),t[0+v]=o+f+128>>8,t[1+v]=g+p+128>>8,t[2+v]=m-l+128>>8,t[3+v]=b-y+128>>8,t[4+v]=b+y+128>>8,t[5+v]=l+m+128>>8,t[6+v]=p-g+128>>8,t[7+v]=f-o+128>>8},t.PICTURE_RATE=[0,23.976,24,25,29.97,30,50,59.94,60,0,0,0,0,0,0,0],t.ZIG_ZAG=new Uint8Array([0,1,8,16,9,2,3,10,17,24,32,25,18,11,4,5,12,19,26,33,40,48,41,34,27,20,13,6,7,14,21,28,35,42,49,56,57,50,43,36,29,22,15,23,30,37,44,51,58,59,52,45,38,31,39,46,53,60,61,54,47,55,62,63]),t.DEFAULT_INTRA_QUANT_MATRIX=new Uint8Array([8,16,19,22,26,27,29,34,16,16,22,24,27,29,34,37,19,22,26,27,29,34,34,38,22,22,26,27,29,34,37,40,22,26,27,29,32,35,40,48,26,27,29,32,35,40,48,58,26,27,29,34,38,46,56,69,27,29,35,38,46,56,69,83]),t.DEFAULT_NON_INTRA_QUANT_MATRIX=new Uint8Array([16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16]),t.PREMULTIPLIER_MATRIX=new Uint8Array([32,44,42,38,32,25,17,9,44,62,58,52,44,35,24,12,42,58,55,49,42,33,23,12,38,52,49,44,38,30,20,10,32,44,42,38,32,25,17,9,25,35,33,30,25,20,14,7,17,24,23,20,17,14,9,5,9,12,12,10,9,7,5,2]),t.MACROBLOCK_ADDRESS_INCREMENT=new Int16Array([3,6,0,9,12,0,0,0,1,15,18,0,21,24,0,27,30,0,33,36,0,0,0,3,0,0,2,39,42,0,45,48,0,0,0,5,0,0,4,51,54,0,57,60,0,0,0,7,0,0,6,63,66,0,69,72,0,75,78,0,81,84,0,-1,87,0,-1,90,0,93,96,0,99,102,0,105,108,0,111,114,0,0,0,9,0,0,8,117,120,0,123,126,0,129,132,0,135,138,0,0,0,15,0,0,14,0,0,13,0,0,12,0,0,11,0,0,10,141,-1,0,-1,144,0,147,150,0,153,156,0,159,162,0,165,168,0,171,174,0,177,180,0,183,-1,0,-1,186,0,189,192,0,195,198,0,201,204,0,207,210,0,213,216,0,219,222,0,0,0,21,0,0,20,0,0,19,0,0,18,0,0,17,0,0,16,0,0,35,0,0,34,0,0,33,0,0,32,0,0,31,0,0,30,0,0,29,0,0,28,0,0,27,0,0,26,0,0,25,0,0,24,0,0,23,0,0,22]),t.MACROBLOCK_TYPE_INTRA=new Int8Array([3,6,0,-1,9,0,0,0,1,0,0,17]),t.MACROBLOCK_TYPE_PREDICTIVE=new Int8Array([3,6,0,9,12,0,0,0,10,15,18,0,0,0,2,21,24,0,0,0,8,27,30,0,33,36,0,-1,39,0,0,0,18,0,0,26,0,0,1,0,0,17]),t.MACROBLOCK_TYPE_B=new Int8Array([3,6,0,9,15,0,12,18,0,24,21,0,0,0,12,27,30,0,0,0,14,39,42,0,36,33,0,0,0,4,0,0,6,54,48,0,45,51,0,0,0,8,0,0,10,-1,57,0,0,0,1,60,63,0,0,0,30,0,0,17,0,0,22,0,0,26]),t.MACROBLOCK_TYPE=[null,t.MACROBLOCK_TYPE_INTRA,t.MACROBLOCK_TYPE_PREDICTIVE,t.MACROBLOCK_TYPE_B],t.CODE_BLOCK_PATTERN=new Int16Array([6,3,0,9,18,0,12,15,0,24,33,0,36,39,0,27,21,0,30,42,0,60,57,0,54,48,0,69,51,0,81,75,0,63,84,0,45,66,0,72,78,0,0,0,60,105,120,0,132,144,0,114,108,0,126,141,0,87,93,0,117,96,0,0,0,32,135,138,0,99,123,0,129,102,0,0,0,4,90,111,0,0,0,8,0,0,16,0,0,44,150,168,0,0,0,28,0,0,52,0,0,62,183,177,0,156,180,0,0,0,1,165,162,0,0,0,61,0,0,56,171,174,0,0,0,2,0,0,40,153,186,0,0,0,48,192,189,0,147,159,0,0,0,20,0,0,12,240,249,0,0,0,63,231,225,0,195,219,0,252,198,0,0,0,24,0,0,36,0,0,3,207,261,0,243,237,0,204,213,0,210,234,0,201,228,0,216,222,0,258,255,0,264,246,0,-1,282,0,285,291,0,0,0,33,0,0,9,318,330,0,306,348,0,0,0,5,0,0,10,279,267,0,0,0,6,0,0,18,0,0,17,0,0,34,339,357,0,309,312,0,270,276,0,327,321,0,351,354,0,303,297,0,294,288,0,300,273,0,342,345,0,315,324,0,336,333,0,363,375,0,0,0,41,0,0,14,0,0,21,372,366,0,360,369,0,0,0,11,0,0,19,0,0,7,0,0,35,0,0,13,0,0,50,0,0,49,0,0,58,0,0,37,0,0,25,0,0,45,0,0,57,0,0,26,0,0,29,0,0,38,0,0,53,0,0,23,0,0,43,0,0,46,0,0,42,0,0,22,0,0,54,0,0,51,0,0,15,0,0,30,0,0,39,0,0,47,0,0,55,0,0,27,0,0,59,0,0,31]),t.MOTION=new Int16Array([3,6,0,12,9,0,0,0,0,18,15,0,24,21,0,0,0,-1,0,0,1,27,30,0,36,33,0,0,0,2,0,0,-2,42,45,0,48,39,0,60,54,0,0,0,3,0,0,-3,51,57,0,-1,69,0,81,75,0,78,63,0,72,66,0,96,84,0,87,93,0,-1,99,0,108,105,0,0,0,-4,90,102,0,0,0,4,0,0,-7,0,0,5,111,123,0,0,0,-5,0,0,7,114,120,0,126,117,0,0,0,-6,0,0,6,153,162,0,150,147,0,135,138,0,156,141,0,129,159,0,132,144,0,0,0,10,0,0,9,0,0,8,0,0,-8,171,198,0,0,0,-9,180,192,0,168,183,0,165,186,0,174,189,0,0,0,-10,177,195,0,0,0,12,0,0,16,0,0,13,0,0,14,0,0,11,0,0,15,0,0,-16,0,0,-12,0,0,-14,0,0,-15,0,0,-11,0,0,-13]),t.DCT_DC_SIZE_LUMINANCE=new Int8Array([6,3,0,18,15,0,9,12,0,0,0,1,0,0,2,27,24,0,21,30,0,0,0,0,36,33,0,0,0,4,0,0,3,39,42,0,0,0,5,0,0,6,48,45,0,51,-1,0,0,0,7,0,0,8]),t.DCT_DC_SIZE_CHROMINANCE=new Int8Array([6,3,0,12,9,0,18,15,0,24,21,0,0,0,2,0,0,1,0,0,0,30,27,0,0,0,3,36,33,0,0,0,4,42,39,0,0,0,5,48,45,0,0,0,6,51,-1,0,0,0,7,0,0,8]),t.DCT_COEFF=new Int32Array([3,6,0,12,9,0,0,0,1,21,24,0,18,15,0,39,27,0,33,30,0,42,36,0,0,0,257,60,66,0,54,63,0,48,57,0,0,0,513,51,45,0,0,0,2,0,0,3,81,75,0,87,93,0,72,78,0,96,90,0,0,0,1025,69,84,0,0,0,769,0,0,258,0,0,1793,0,0,65535,0,0,1537,111,108,0,0,0,1281,105,102,0,117,114,0,99,126,0,120,123,0,156,150,0,162,159,0,144,147,0,129,135,0,138,132,0,0,0,2049,0,0,4,0,0,514,0,0,2305,153,141,0,165,171,0,180,168,0,177,174,0,183,186,0,0,0,2561,0,0,3329,0,0,6,0,0,259,0,0,5,0,0,770,0,0,2817,0,0,3073,228,225,0,201,210,0,219,213,0,234,222,0,216,231,0,207,192,0,204,189,0,198,195,0,243,261,0,273,240,0,246,237,0,249,258,0,279,276,0,252,255,0,270,282,0,264,267,0,0,0,515,0,0,260,0,0,7,0,0,1026,0,0,1282,0,0,4097,0,0,3841,0,0,3585,315,321,0,333,342,0,312,291,0,375,357,0,288,294,0,-1,369,0,285,303,0,318,363,0,297,306,0,339,309,0,336,348,0,330,300,0,372,345,0,351,366,0,327,354,0,360,324,0,381,408,0,417,420,0,390,378,0,435,438,0,384,387,0,0,0,2050,396,402,0,465,462,0,0,0,8,411,399,0,429,432,0,453,414,0,426,423,0,0,0,10,0,0,9,0,0,11,0,0,5377,0,0,1538,0,0,771,0,0,5121,0,0,1794,0,0,4353,0,0,4609,0,0,4865,444,456,0,0,0,1027,459,450,0,0,0,261,393,405,0,0,0,516,447,441,0,516,519,0,486,474,0,510,483,0,504,498,0,471,537,0,507,501,0,522,513,0,534,531,0,468,477,0,492,495,0,549,546,0,525,528,0,0,0,263,0,0,2562,0,0,2306,0,0,5633,0,0,5889,0,0,6401,0,0,6145,0,0,1283,0,0,772,0,0,13,0,0,12,0,0,14,0,0,15,0,0,517,0,0,6657,0,0,262,540,543,0,480,489,0,588,597,0,0,0,27,609,555,0,606,603,0,0,0,19,0,0,22,591,621,0,0,0,18,573,576,0,564,570,0,0,0,20,552,582,0,0,0,21,558,579,0,0,0,23,612,594,0,0,0,25,0,0,24,600,615,0,0,0,31,0,0,30,0,0,28,0,0,29,0,0,26,0,0,17,0,0,16,567,618,0,561,585,0,654,633,0,0,0,37,645,648,0,0,0,36,630,636,0,0,0,34,639,627,0,663,666,0,657,624,0,651,642,0,669,660,0,0,0,35,0,0,267,0,0,40,0,0,268,0,0,266,0,0,32,0,0,264,0,0,265,0,0,38,0,0,269,0,0,270,0,0,33,0,0,39,0,0,7937,0,0,6913,0,0,7681,0,0,4098,0,0,7425,0,0,7169,0,0,271,0,0,274,0,0,273,0,0,272,0,0,1539,0,0,2818,0,0,3586,0,0,3330,0,0,3074,0,0,3842]),t.PICTURE_TYPE={INTRA:1,PREDICTIVE:2,B:3},t.START={SEQUENCE:179,SLICE_FIRST:1,SLICE_LAST:175,PICTURE:0,EXTENSION:181,USER_DATA:178},t}(),P.Decoder.MP2Audio=function(){var t=function(e){P.Decoder.Base.call(this,e);var i=e.audioBufferSize||131072,r=e.streaming?P.BitBuffer.MODE.EVICT:P.BitBuffer.MODE.EXPAND;this.bits=new P.BitBuffer(i,r),this.left=new Float32Array(1152),this.right=new Float32Array(1152),this.sampleRate=44100,this.D=new Float32Array(1024),this.D.set(t.SYNTHESIS_WINDOW,0),this.D.set(t.SYNTHESIS_WINDOW,512),this.V=new Float32Array(1024),this.U=new Int32Array(32),this.VPos=0,this.allocation=[new Array(32),new Array(32)],this.scaleFactorInfo=[new Uint8Array(32),new Uint8Array(32)],this.scaleFactor=[new Array(32),new Array(32)],this.sample=[new Array(32),new Array(32)];for(var s=0;s<2;s++)for(var o=0;o<32;o++)this.scaleFactor[s][o]=[0,0,0],this.sample[s][o]=[0,0,0]};return t.prototype=Object.create(P.Decoder.Base.prototype),t.prototype.constructor=t,t.prototype.decode=function(){var t=this.bits.index>>3;if(t>=this.bits.byteLength)return!1;var e=this.decodeFrame(this.left,this.right);return this.bits.index=t+e<<3,!!e&&(this.destination&&(this.decodedFrameCnt++,this.destination.play(this.sampleRate,this.left,this.right)),this.advanceDecodedTime(this.left.length/this.sampleRate),!0)},t.prototype.getCurrentTime=function(){var t=this.destination?this.destination.enqueuedTime:0;return this.decodedTime-t},t.prototype.decodeFrame=function(e,i){var r=this.bits.read(11),s=this.bits.read(2),o=this.bits.read(2),n=!this.bits.read(1);if(r!==t.FRAME_SYNC||s!==t.VERSION.MPEG_1||o!==t.LAYER.II)return 0;var a=this.bits.read(4)-1;if(a>13)return 0;var h=this.bits.read(2),l=t.SAMPLE_RATE[h];if(3===h)return 0;s===t.VERSION.MPEG_2&&(h+=4,a+=14);var d=this.bits.read(1),u=(this.bits.read(1),this.bits.read(2)),c=0;u===t.MODE.JOINT_STEREO?c=this.bits.read(2)+1<<2:(this.bits.skip(2),c=u===t.MODE.MONO?0:32),this.bits.skip(4),n&&this.bits.skip(16);var g=144e3*t.BIT_RATE[a]/(l=t.SAMPLE_RATE[h])+d|0,p=0,f=0;if(s===t.VERSION.MPEG_2)p=2,f=30;else{var m=u===t.MODE.MONO?0:1,b=t.QUANT_LUT_STEP_1[m][a];f=63&(p=t.QUANT_LUT_STEP_2[b][h]),p>>=6}c>f&&(c=f);for(v=0;v<c;v++)this.allocation[0][v]=this.readAllocation(v,p),this.allocation[1][v]=this.readAllocation(v,p);for(v=c;v<f;v++)this.allocation[0][v]=this.allocation[1][v]=this.readAllocation(v,p);for(var y=u===t.MODE.MONO?1:2,v=0;v<f;v++){for(E=0;E<y;E++)this.allocation[E][v]&&(this.scaleFactorInfo[E][v]=this.bits.read(2));u===t.MODE.MONO&&(this.scaleFactorInfo[1][v]=this.scaleFactorInfo[0][v])}for(v=0;v<f;v++){for(E=0;E<y;E++)if(this.allocation[E][v]){var T=this.scaleFactor[E][v];switch(this.scaleFactorInfo[E][v]){case 0:T[0]=this.bits.read(6),T[1]=this.bits.read(6),T[2]=this.bits.read(6);break;case 1:T[0]=T[1]=this.bits.read(6),T[2]=this.bits.read(6);break;case 2:T[0]=T[1]=T[2]=this.bits.read(6);break;case 3:T[0]=this.bits.read(6),T[1]=T[2]=this.bits.read(6)}}u===t.MODE.MONO&&(this.scaleFactor[1][v][0]=this.scaleFactor[0][v][0],this.scaleFactor[1][v][1]=this.scaleFactor[0][v][1],this.scaleFactor[1][v][2]=this.scaleFactor[0][v][2])}for(var w=0,_=0;_<3;_++)for(var C=0;C<4;C++){for(v=0;v<c;v++)this.readSamples(0,v,_),this.readSamples(1,v,_);for(v=c;v<f;v++)this.readSamples(0,v,_),this.sample[1][v][0]=this.sample[0][v][0],this.sample[1][v][1]=this.sample[0][v][1],this.sample[1][v][2]=this.sample[0][v][2];for(v=f;v<32;v++)this.sample[0][v][0]=0,this.sample[0][v][1]=0,this.sample[0][v][2]=0,this.sample[1][v][0]=0,this.sample[1][v][1]=0,this.sample[1][v][2]=0;for(var S=0;S<3;S++){this.VPos=this.VPos-64&1023;for(var E=0;E<2;E++){t.MatrixTransform(this.sample[E],S,this.V,this.VPos),P.Fill(this.U,0);for(var A=512-(this.VPos>>1),k=this.VPos%128>>1;k<1024;){for(R=0;R<32;++R)this.U[R]+=this.D[A++]*this.V[k++];k+=96,A+=32}for(k=1120-k,A-=480;k<1024;){for(var R=0;R<32;++R)this.U[R]+=this.D[A++]*this.V[k++];k+=96,A+=32}for(var x=0===E?e:i,L=0;L<32;L++)x[w+L]=this.U[L]/2147418112}w+=32}}return this.sampleRate=l,g},t.prototype.readAllocation=function(e,i){var r=t.QUANT_LUT_STEP_3[i][e],s=t.QUANT_LUT_STEP4[15&r][this.bits.read(r>>4)];return s?t.QUANT_TAB[s-1]:0},t.prototype.readSamples=function(e,i,r){var s=this.allocation[e][i],o=this.scaleFactor[e][i][r],n=this.sample[e][i],a=0;if(s){if(63===o)o=0;else{var h=o/3|0;o=t.SCALEFACTOR_BASE[o%3]+(1<<h>>1)>>h}var l=s.levels;s.group?(a=this.bits.read(s.bits),n[0]=a%l,a=a/l|0,n[1]=a%l,n[2]=a/l|0):(n[0]=this.bits.read(s.bits),n[1]=this.bits.read(s.bits),n[2]=this.bits.read(s.bits));var d=65536/(l+1)|0;a=((l=(l+1>>1)-1)-n[0])*d,n[0]=a*(o>>12)+(a*(4095&o)+2048>>12)>>12,a=(l-n[1])*d,n[1]=a*(o>>12)+(a*(4095&o)+2048>>12)>>12,a=(l-n[2])*d,n[2]=a*(o>>12)+(a*(4095&o)+2048>>12)>>12}else n[0]=n[1]=n[2]=0},t.MatrixTransform=function(t,e,i,r){var s,o,n,a,h,l,d,u,c,g,p,f,m,b,y,v,T,w,_,C,S,E,A,k,R,x,L,D,P,I,U,F,N;s=t[0][e]+t[31][e],o=.500602998235*(t[0][e]-t[31][e]),n=t[1][e]+t[30][e],a=.505470959898*(t[1][e]-t[30][e]),h=t[2][e]+t[29][e],l=.515447309923*(t[2][e]-t[29][e]),d=t[3][e]+t[28][e],u=.53104259109*(t[3][e]-t[28][e]),c=t[4][e]+t[27][e],g=.553103896034*(t[4][e]-t[27][e]),p=t[5][e]+t[26][e],f=.582934968206*(t[5][e]-t[26][e]),m=t[6][e]+t[25][e],b=.622504123036*(t[6][e]-t[25][e]),y=t[7][e]+t[24][e],v=.674808341455*(t[7][e]-t[24][e]),T=t[8][e]+t[23][e],w=.744536271002*(t[8][e]-t[23][e]),_=t[9][e]+t[22][e],C=.839349645416*(t[9][e]-t[22][e]),S=t[10][e]+t[21][e],E=.972568237862*(t[10][e]-t[21][e]),A=t[11][e]+t[20][e],k=1.16943993343*(t[11][e]-t[20][e]),R=t[12][e]+t[19][e],x=1.48416461631*(t[12][e]-t[19][e]),L=t[13][e]+t[18][e],D=2.05778100995*(t[13][e]-t[18][e]),P=t[14][e]+t[17][e],I=3.40760841847*(t[14][e]-t[17][e]),N=s+(U=t[15][e]+t[16][e]),U=.502419286188*(s-U),s=n+P,P=.52249861494*(n-P),n=h+L,L=.566944034816*(h-L),h=d+R,R=.64682178336*(d-R),d=c+A,A=.788154623451*(c-A),c=p+S,S=1.06067768599*(p-S),p=m+_,_=1.72244709824*(m-_),m=y+T,T=5.10114861869*(y-T),y=N+m,m=.509795579104*(N-m),N=s+p,s=.601344886935*(s-p),p=n+c,c=.899976223136*(n-c),n=h+d,d=2.56291544774*(h-d),h=y+n,y=.541196100146*(y-n),n=N+p,p=1.30656296488*(N-p),N=h+n,h=.707106781187*(h-n),n=y+p,n+=y=.707106781187*(y-p),p=m+d,m=.541196100146*(m-d),d=s+c,c=1.30656296488*(s-c),s=p+d,d=.707106781187*(p-d),p=m+c,s+=p+=m=.707106781187*(m-c),p+=d,d+=m,c=U+T,U=.509795579104*(U-T),T=P+_,P=.601344886935*(P-_),_=L+S,S=.899976223136*(L-S),L=R+A,A=2.56291544774*(R-A),R=c+L,c=.541196100146*(c-L),L=T+_,_=1.30656296488*(T-_),T=R+L,L=.707106781187*(R-L),R=c+_,_=.707106781187*(c-_),c=U+A,U=.541196100146*(U-A),A=P+S,S=1.30656296488*(P-S),P=c+A,A=.707106781187*(c-A),c=U+S,T+=P+=c+=U=.707106781187*(U-S),P+=R+=_,R+=c+=A,c+=L,L+=A+=U,A+=_,_+=U,S=o+(F=10.1900081235*(t[15][e]-t[16][e])),o=.502419286188*(o-F),F=a+I,a=.52249861494*(a-I),I=l+D,D=.566944034816*(l-D),l=u+x,u=.64682178336*(u-x),x=g+k,g=.788154623451*(g-k),k=f+E,E=1.06067768599*(f-E),f=b+C,C=1.72244709824*(b-C),b=v+w,v=5.10114861869*(v-w),w=S+b,b=.509795579104*(S-b),S=F+f,F=.601344886935*(F-f),f=I+k,k=.899976223136*(I-k),I=l+x,x=2.56291544774*(l-x),l=w+I,w=.541196100146*(w-I),I=S+f,f=1.30656296488*(S-f),S=l+I,I=.707106781187*(l-I),l=w+f,f=.707106781187*(w-f),w=b+x,x=.541196100146*(b-x),b=F+k,k=1.30656296488*(F-k),F=w+b,b=.707106781187*(w-b),w=x+k,F+=w+=k=.707106781187*(x-k),w+=b,x=b+k,b=o+v,o=.509795579104*(o-v),v=a+C,a=.601344886935*(a-C),C=D+E,E=.899976223136*(D-E),D=u+g,g=2.56291544774*(u-g),u=b+D,b=.541196100146*(b-D),D=v+C,C=1.30656296488*(v-C),v=u+D,D=.707106781187*(u-D),u=b+C,C=.707106781187*(b-C),b=o+g,o=.541196100146*(o-g),g=a+E,E=1.30656296488*(a-E),a=b+g,g=.707106781187*(b-g),b=o+E,S+=v+=a+=b+=o=.707106781187*(o-E),v+=F,F+=a+=u+=C,a+=l+=f,l+=u+=b+=g,u+=w,w+=b+=D,b+=I,I+=D+=g+=o,D+=x,x+=g+=C,g+=f,f+=C+=o,C+=k,k+=o,i[r+48]=-N,i[r+49]=i[r+47]=-S,i[r+50]=i[r+46]=-T,i[r+51]=i[r+45]=-v,i[r+52]=i[r+44]=-s,i[r+53]=i[r+43]=-F,i[r+54]=i[r+42]=-P,i[r+55]=i[r+41]=-a,i[r+56]=i[r+40]=-n,i[r+57]=i[r+39]=-l,i[r+58]=i[r+38]=-R,i[r+59]=i[r+37]=-u,i[r+60]=i[r+36]=-p,i[r+61]=i[r+35]=-w,i[r+62]=i[r+34]=-c,i[r+63]=i[r+33]=-b,i[r+32]=-h,i[r+0]=h,i[r+31]=-I,i[r+1]=I,i[r+30]=-L,i[r+2]=L,i[r+29]=-D,i[r+3]=D,i[r+28]=-d,i[r+4]=d,i[r+27]=-x,i[r+5]=x,i[r+26]=-A,i[r+6]=A,i[r+25]=-g,i[r+7]=g,i[r+24]=-y,i[r+8]=y,i[r+23]=-f,i[r+9]=f,i[r+22]=-_,i[r+10]=_,i[r+21]=-C,i[r+11]=C,i[r+20]=-m,i[r+12]=m,i[r+19]=-k,i[r+13]=k,i[r+18]=-U,i[r+14]=U,i[r+17]=-o,i[r+15]=o,i[r+16]=0},t.FRAME_SYNC=2047,t.VERSION={MPEG_2_5:0,MPEG_2:2,MPEG_1:3},t.LAYER={III:1,II:2,I:3},t.MODE={STEREO:0,JOINT_STEREO:1,DUAL_CHANNEL:2,MONO:3},t.SAMPLE_RATE=new Uint16Array([44100,48e3,32e3,0,22050,24e3,16e3,0]),t.BIT_RATE=new Uint16Array([32,48,56,64,80,96,112,128,160,192,224,256,320,384,8,16,24,32,40,48,56,64,80,96,112,128,144,160]),t.SCALEFACTOR_BASE=new Uint32Array([33554432,26632170,21137968]),t.SYNTHESIS_WINDOW=new Float32Array([0,-.5,-.5,-.5,-.5,-.5,-.5,-1,-1,-1,-1,-1.5,-1.5,-2,-2,-2.5,-2.5,-3,-3.5,-3.5,-4,-4.5,-5,-5.5,-6.5,-7,-8,-8.5,-9.5,-10.5,-12,-13,-14.5,-15.5,-17.5,-19,-20.5,-22.5,-24.5,-26.5,-29,-31.5,-34,-36.5,-39.5,-42.5,-45.5,-48.5,-52,-55.5,-58.5,-62.5,-66,-69.5,-73.5,-77,-80.5,-84.5,-88,-91.5,-95,-98,-101,-104,106.5,109,111,112.5,113.5,114,114,113.5,112,110.5,107.5,104,100,94.5,88.5,81.5,73,63.5,53,41.5,28.5,14.5,-1,-18,-36,-55.5,-76.5,-98.5,-122,-147,-173.5,-200.5,-229.5,-259.5,-290.5,-322.5,-355.5,-389.5,-424,-459.5,-495.5,-532,-568.5,-605,-641.5,-678,-714,-749,-783.5,-817,-849,-879.5,-908.5,-935,-959.5,-981,-1000.5,-1016,-1028.5,-1037.5,-1042.5,-1043.5,-1040,-1031.5,1018.5,1e3,976,946.5,911,869.5,822,767.5,707,640,565.5,485,397,302.5,201,92.5,-22.5,-144,-272.5,-407,-547.5,-694,-846,-1003,-1165,-1331.5,-1502,-1675.5,-1852.5,-2031.5,-2212.5,-2394,-2576.5,-2758.5,-2939.5,-3118.5,-3294.5,-3467.5,-3635.5,-3798.5,-3955,-4104.5,-4245.5,-4377.5,-4499,-4609.5,-4708,-4792.5,-4863.5,-4919,-4958,-4979.5,-4983,-4967.5,-4931.5,-4875,-4796,-4694.5,-4569.5,-4420,-4246,-4046,-3820,-3567,3287,2979.5,2644,2280.5,1888,1467.5,1018.5,541,35,-499,-1061,-1650,-2266.5,-2909,-3577,-4270,-4987.5,-5727.5,-6490,-7274,-8077.5,-8899.5,-9739,-10594.5,-11464.5,-12347,-13241,-14144.5,-15056,-15973.5,-16895.5,-17820,-18744.5,-19668,-20588,-21503,-22410.5,-23308.5,-24195,-25068.5,-25926.5,-26767,-27589,-28389,-29166.5,-29919,-30644.5,-31342,-32009.5,-32645,-33247,-33814.5,-34346,-34839.5,-35295,-35710,-36084.5,-36417.5,-36707.5,-36954,-37156.5,-37315,-37428,-37496,37519,37496,37428,37315,37156.5,36954,36707.5,36417.5,36084.5,35710,35295,34839.5,34346,33814.5,33247,32645,32009.5,31342,30644.5,29919,29166.5,28389,27589,26767,25926.5,25068.5,24195,23308.5,22410.5,21503,20588,19668,18744.5,17820,16895.5,15973.5,15056,14144.5,13241,12347,11464.5,10594.5,9739,8899.5,8077.5,7274,6490,5727.5,4987.5,4270,3577,2909,2266.5,1650,1061,499,-35,-541,-1018.5,-1467.5,-1888,-2280.5,-2644,-2979.5,3287,3567,3820,4046,4246,4420,4569.5,4694.5,4796,4875,4931.5,4967.5,4983,4979.5,4958,4919,4863.5,4792.5,4708,4609.5,4499,4377.5,4245.5,4104.5,3955,3798.5,3635.5,3467.5,3294.5,3118.5,2939.5,2758.5,2576.5,2394,2212.5,2031.5,1852.5,1675.5,1502,1331.5,1165,1003,846,694,547.5,407,272.5,144,22.5,-92.5,-201,-302.5,-397,-485,-565.5,-640,-707,-767.5,-822,-869.5,-911,-946.5,-976,-1e3,1018.5,1031.5,1040,1043.5,1042.5,1037.5,1028.5,1016,1000.5,981,959.5,935,908.5,879.5,849,817,783.5,749,714,678,641.5,605,568.5,532,495.5,459.5,424,389.5,355.5,322.5,290.5,259.5,229.5,200.5,173.5,147,122,98.5,76.5,55.5,36,18,1,-14.5,-28.5,-41.5,-53,-63.5,-73,-81.5,-88.5,-94.5,-100,-104,-107.5,-110.5,-112,-113.5,-114,-114,-113.5,-112.5,-111,-109,106.5,104,101,98,95,91.5,88,84.5,80.5,77,73.5,69.5,66,62.5,58.5,55.5,52,48.5,45.5,42.5,39.5,36.5,34,31.5,29,26.5,24.5,22.5,20.5,19,17.5,15.5,14.5,13,12,10.5,9.5,8.5,8,7,6.5,5.5,5,4.5,4,3.5,3.5,3,2.5,2.5,2,2,1.5,1.5,1,1,1,1,.5,.5,.5,.5,.5,.5]),t.QUANT_LUT_STEP_1=[[0,0,1,1,1,2,2,2,2,2,2,2,2,2],[0,0,0,0,0,0,1,1,1,2,2,2,2,2]],t.QUANT_TAB={A:91,B:94,C:8,D:12},t.QUANT_LUT_STEP_2=[[t.QUANT_TAB.C,t.QUANT_TAB.C,t.QUANT_TAB.D],[t.QUANT_TAB.A,t.QUANT_TAB.A,t.QUANT_TAB.A],[t.QUANT_TAB.B,t.QUANT_TAB.A,t.QUANT_TAB.B]],t.QUANT_LUT_STEP_3=[[68,68,52,52,52,52,52,52,52,52,52,52],[67,67,67,66,66,66,66,66,66,66,66,49,49,49,49,49,49,49,49,49,49,49,49,32,32,32,32,32,32,32],[69,69,69,69,52,52,52,52,52,52,52,36,36,36,36,36,36,36,36,36,36,36,36,36,36,36,36,36,36,36]],t.QUANT_LUT_STEP4=[[0,1,2,17],[0,1,2,3,4,5,6,17],[0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,17],[0,1,3,5,6,7,8,9,10,11,12,13,14,15,16,17],[0,1,2,4,5,6,7,8,9,10,11,12,13,14,15,17],[0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15]],t.QUANT_TAB=[{levels:3,group:1,bits:5},{levels:5,group:1,bits:7},{levels:7,group:0,bits:3},{levels:9,group:1,bits:10},{levels:15,group:0,bits:4},{levels:31,group:0,bits:5},{levels:63,group:0,bits:6},{levels:127,group:0,bits:7},{levels:255,group:0,bits:8},{levels:511,group:0,bits:9},{levels:1023,group:0,bits:10},{levels:2047,group:0,bits:11},{levels:4095,group:0,bits:12},{levels:8191,group:0,bits:13},{levels:16383,group:0,bits:14},{levels:32767,group:0,bits:15},{levels:65535,group:0,bits:16}],t}(),P.Renderer.WebGL=function(){var t=function(e){this.canvas=e.canvas,this.canvas.width=this.canvas.clientWidth,this.canvas.height=this.canvas.clientHeight,this.width=this.canvas.width,this.height=this.canvas.height,this.fixWidth=this.width,this.enabled=!0,this._error=void 0,Object.defineProperty(this,"error",{get:this.getRenderError});var i={preserveDrawingBuffer:!!e.preserveDrawingBuffer,alpha:!1,depth:!1,stencil:!1,antialias:!1};if(this.gl=this.canvas.getContext("webgl",i)||this.canvas.getContext("experimental-webgl",i),!this.gl)throw new Error("Failed to get WebGL Context");var r=this.gl;try{this.bufferConvertVertex=r.createBuffer();var s=new Float32Array([0,0,0,1,1,0,1,1]);r.bindBuffer(r.ARRAY_BUFFER,this.bufferConvertVertex),r.bufferData(r.ARRAY_BUFFER,s,r.STATIC_DRAW),r.bindBuffer(r.ARRAY_BUFFER,null),this.convertProgram=this.createProgram(t.SHADER.VERTEX_IDENTITY,t.SHADER.FRAGMENT_YCRCB_TO_RGBA),this.attrConvertVertex=r.getAttribLocation(this.convertProgram,"vertex"),this.uniformY=r.getUniformLocation(this.convertProgram,"textureY"),this.uniformCb=r.getUniformLocation(this.convertProgram,"textureCb"),this.uniformCr=r.getUniformLocation(this.convertProgram,"textureCr"),this.textureY=this.createTexture(0,"textureY",this.convertProgram),this.textureCb=this.createTexture(1,"textureCb",this.convertProgram),this.textureCr=this.createTexture(2,"textureCr",this.convertProgram),this.loadingProgram=this.createProgram(t.SHADER.VERTEX_IDENTITY,t.SHADER.FRAGMENT_LOADING),this.bufferBlitVertex=r.createBuffer(),this.bufferBlitTexCoord=r.createBuffer(),this.blitProgram=this.createProgram(t.SHADER.VERTEX_BLIT,t.SHADER.FRAGMENT_BLIT),this.attrBlitVertex=r.getAttribLocation(this.blitProgram,"vertex"),this.attrBlitTexCoord=r.getAttribLocation(this.blitProgram,"texCoord"),this.uniformRgb=r.getUniformLocation(this.blitProgram,"textureRgb"),this.fb=r.createFramebuffer()}catch(t){this._error=t}this.shouldCreateUnclampedViews=!this.allowsClampedTextureData(),this.viewMode=e.viewMode||0};return t.prototype.destroy=function(){var t=this.gl;t.deleteTexture(this.textureY),t.deleteTexture(this.textureCb),t.deleteTexture(this.textureCr),void 0!==this.textureRgb&&(t.deleteTexture(this.textureRgb),this.textureRgb=void 0),t.deleteProgram(this.convertProgram),t.deleteProgram(this.loadingProgram),t.deleteProgram(this.blitProgram),t.deleteBuffer(this.bufferConvertVertex),t.deleteBuffer(this.bufferBlitVertex),t.deleteBuffer(this.bufferBlitTexCoord),t.deleteFramebuffer(this.fb)},t.prototype.resize=function(t,e){if(void 0===this._error){this.width=0|t,this.height=0|e,this.fixWidth=this.width+15>>4<<4,console.info("width:",t),console.info("height:",e);var i=this.gl;i.bindFramebuffer(i.FRAMEBUFFER,this.fb),void 0!==this.textureRgb&&(i.deleteTexture(this.textureRgb),this.textureRgb=void 0),this.textureRgb=this.createTexture(3,"textureRgb",this.blitProgram),i.texImage2D(i.TEXTURE_2D,0,i.RGB,this.fixWidth,this.height,0,i.RGB,i.UNSIGNED_BYTE,null),i.framebufferTexture2D(i.FRAMEBUFFER,i.COLOR_ATTACHMENT0,i.TEXTURE_2D,this.textureRgb,0);var r=1,s=1,o=this.width/this.height,n=this.canvas.width/this.canvas.height;0===this.viewMode?o>n?s=this.canvas.width/o/this.canvas.height:r=this.canvas.height*o/this.canvas.width:1===this.viewMode?o>n?r=this.canvas.height*o/this.canvas.width:s=this.canvas.width/o/this.canvas.height:this.viewMode,i.bindBuffer(i.ARRAY_BUFFER,this.bufferBlitVertex);var a=new Float32Array([-1*r,-1*s,r,-1*s,-1*r,s,r,s]);i.bufferData(i.ARRAY_BUFFER,a,i.STATIC_DRAW),i.bindBuffer(i.ARRAY_BUFFER,null);var h=this.width/this.fixWidth;i.bindBuffer(i.ARRAY_BUFFER,this.bufferBlitTexCoord);var l=new Float32Array([0,0,h,0,0,1,h,1]);i.bufferData(i.ARRAY_BUFFER,l,i.STATIC_DRAW),i.bindBuffer(i.ARRAY_BUFFER,null)}},t.prototype.createTexture=function(t,e,i){var r=this.gl,s=r.createTexture();return r.bindTexture(r.TEXTURE_2D,s),r.texParameteri(r.TEXTURE_2D,r.TEXTURE_MAG_FILTER,r.LINEAR),r.texParameteri(r.TEXTURE_2D,r.TEXTURE_MIN_FILTER,r.LINEAR),r.texParameteri(r.TEXTURE_2D,r.TEXTURE_WRAP_S,r.CLAMP_TO_EDGE),r.texParameteri(r.TEXTURE_2D,r.TEXTURE_WRAP_T,r.CLAMP_TO_EDGE),s},t.prototype.createProgram=function(t,e){var i=this.gl,r=i.createProgram();return i.attachShader(r,this.compileShader(i.VERTEX_SHADER,t)),i.attachShader(r,this.compileShader(i.FRAGMENT_SHADER,e)),i.linkProgram(r),i.useProgram(r),r},t.prototype.compileShader=function(t,e){var i=this.gl,r=i.createShader(t);if(i.shaderSource(r,e),i.compileShader(r),!i.getShaderParameter(r,i.COMPILE_STATUS))throw new Error(i.getShaderInfoLog(r));return r},t.prototype.allowsClampedTextureData=function(){var t=this.gl,e=t.createTexture();return t.bindTexture(t.TEXTURE_2D,e),t.texImage2D(t.TEXTURE_2D,0,t.LUMINANCE,1,1,0,t.LUMINANCE,t.UNSIGNED_BYTE,new Uint8ClampedArray([0])),0===t.getError()},t.prototype.renderProgress=function(t){return},t.prototype.render=function(t,e,i){if(this.enabled&&void 0===this._error){var r=this.gl,s=this.fixWidth,o=this.height,n=s>>1,a=o>>1;r.bindFramebuffer(r.FRAMEBUFFER,this.fb),r.viewport(0,0,this.fixWidth,this.height),r.clearColor(0,0,0,1),r.clear(r.COLOR_BUFFER_BIT),r.useProgram(this.convertProgram),this.shouldCreateUnclampedViews&&(t=new Uint8Array(t.buffer),e=new Uint8Array(e.buffer),i=new Uint8Array(i.buffer)),this.updateTexture(r.TEXTURE0,this.textureY,s,o,t),this.updateTexture(r.TEXTURE1,this.textureCb,n,a,e),this.updateTexture(r.TEXTURE2,this.textureCr,n,a,i),r.uniform1i(this.uniformY,0),r.uniform1i(this.uniformCb,1),r.uniform1i(this.uniformCr,2),r.bindBuffer(r.ARRAY_BUFFER,this.bufferConvertVertex),r.enableVertexAttribArray(this.attrConvertVertex),r.vertexAttribPointer(this.attrConvertVertex,2,r.FLOAT,!1,0,0),r.drawArrays(r.TRIANGLE_STRIP,0,4),r.bindBuffer(r.ARRAY_BUFFER,null),r.bindFramebuffer(r.FRAMEBUFFER,null),r.viewport(0,0,this.canvas.width,this.canvas.height),r.clearColor(0,0,0,1),r.clear(r.COLOR_BUFFER_BIT),r.useProgram(this.blitProgram),r.activeTexture(r.TEXTURE3),r.bindTexture(r.TEXTURE_2D,this.textureRgb),r.uniform1i(this.uniformRgb,3),r.bindBuffer(r.ARRAY_BUFFER,this.bufferBlitVertex),r.enableVertexAttribArray(this.attrBlitVertex),r.vertexAttribPointer(this.attrBlitVertex,2,r.FLOAT,!1,0,0),r.bindBuffer(r.ARRAY_BUFFER,this.bufferBlitTexCoord),r.enableVertexAttribArray(this.attrBlitTexCoord),r.vertexAttribPointer(this.attrBlitTexCoord,2,r.FLOAT,!1,0,0),r.drawArrays(r.TRIANGLE_STRIP,0,4),r.bindBuffer(r.ARRAY_BUFFER,null)}},t.prototype.updateTexture=function(t,e,i,r,s){var o=this.gl;o.activeTexture(t),o.bindTexture(o.TEXTURE_2D,e),o.texImage2D(o.TEXTURE_2D,0,o.LUMINANCE,i,r,0,o.LUMINANCE,o.UNSIGNED_BYTE,s)},t.prototype.getRenderError=function(){return this._error},t.prototype.enable=function(t){this.enabled=t},t.IsSupported=function(){try{if(!window.WebGLRenderingContext)return!1;var t=document.createElement("canvas");return!(!t.getContext("webgl")&&!t.getContext("experimental-webgl"))}catch(t){return!1}},t.SHADER={FRAGMENT_YCRCB_TO_RGBA:["precision mediump float;","uniform sampler2D textureY;","uniform sampler2D textureCb;","uniform sampler2D textureCr;","varying vec2 texCoord;","mat4 rec601 = mat4(","1.16438,  0.00000,  1.59603, -0.87079,","1.16438, -0.39176, -0.81297,  0.52959,","1.16438,  2.01723,  0.00000, -1.08139,","0, 0, 0, 1",");","void main() {","float y = texture2D(textureY, texCoord).r;","float cb = texture2D(textureCb, texCoord).r;","float cr = texture2D(textureCr, texCoord).r;","gl_FragColor = vec4(y, cr, cb, 1.0) * rec601;","}"].join("\n"),FRAGMENT_LOADING:["precision mediump float;","uniform float progress;","varying vec2 texCoord;","void main() {","float c = ceil(progress-(1.0-texCoord.y));","gl_FragColor = vec4(c,c,c,1);","}"].join("\n"),VERTEX_IDENTITY:["attribute vec2 vertex;","varying vec2 texCoord;","void main() {","texCoord = vertex;","gl_Position = vec4((vertex * 2.0 - 1.0) * vec2(1, -1), 0.0, 1.0);","}"].join("\n"),FRAGMENT_BLIT:["precision mediump float;","uniform sampler2D textureRgb;","varying vec2 textureCoordinate;","void main() {","gl_FragColor = texture2D(textureRgb, textureCoordinate);","}"].join("\n"),VERTEX_BLIT:["attribute vec4 vertex;","attribute vec4 texCoord;","varying vec2 textureCoordinate;","void main() {","textureCoordinate = texCoord.xy;","gl_Position = vertex;","}"].join("\n")},t}(),P.Renderer.Canvas2D=function(){var t=function(t){this.canvas=t.canvas||document.createElement("canvas"),this.width=this.canvas.width,this.height=this.canvas.height,this.enabled=!0,Object.defineProperty(this,"error",{get:this.getRenderError});try{this.context=this.canvas.getContext("2d")}catch(t){this._error=t}this.viewMode=t.viewMode||0};return t.prototype.destroy=function(){},t.prototype.resize=function(t,e){this.width=0|t,this.height=0|e;var i=this.width,r=this.height,s=this.width/this.height,o=this.canvas.clientWidth/this.canvas.clientHeight;0===this.viewMode?s<o?i=r*o:r=i/o:1===this.viewMode?s<o?r=i/o:i=r*o:this.viewMode,console.info("imgAR:",s),console.info("viewAR:",o),console.info("viewWidth:",i),console.info("viewHeight:",r),this.canvas.width=i,this.canvas.height=r,this.imageData=this.context.createImageData(this.width,this.height),P.Fill(this.imageData.data,255)},t.prototype.renderProgress=function(t){var e=this.canvas.width,i=this.canvas.height,r=this.context;r.fillStyle="#222",r.fillRect(0,0,e,i),r.fillStyle="#fff",r.fillRect(0,i-i*t,e,i*t)},t.prototype.render=function(t,e,i){this.YCbCrToRGBA(t,e,i,this.imageData.data);var r=0,s=0;if(0===this.viewMode)r=(this.canvas.width-this.width)/2,s=(this.canvas.height-this.height)/2,this.context.fillStyle="black",this.context.fillRect(0,0,this.canvas.width,this.canvas.height),this.context.putImageData(this.imageData,r,s,0,0,this.width,this.height);else if(1===this.viewMode){r=(this.width-this.canvas.width)/2,s=(this.height-this.canvas.height)/2;this.context.putImageData(this.imageData,0,0,0,0,this.canvas.width,this.canvas.height)}else 2===this.viewMode&&this.context.putImageData(this.imageData,0,0)},t.prototype.YCbCrToRGBA=function(t,e,i,r){if(this.enabled)for(var s,o,n,a,h,l=this.width+15>>4<<4,d=l>>1,u=0,c=l,g=l+(l-this.width),p=0,f=d-(this.width>>1),m=0,b=4*this.width,y=4*this.width,v=this.width>>1,T=this.height>>1,w=0;w<T;w++){for(var _=0;_<v;_++){s=e[p],o=i[p],p++,n=s+(103*s>>8)-179,a=(88*o>>8)-44+(183*s>>8)-91,h=o+(198*o>>8)-227;var C=t[u++],S=t[u++];r[m]=C+n,r[m+1]=C-a,r[m+2]=C+h,r[m+4]=S+n,r[m+5]=S-a,r[m+6]=S+h,m+=8;var E=t[c++],A=t[c++];r[b]=E+n,r[b+1]=E-a,r[b+2]=E+h,r[b+4]=A+n,r[b+5]=A-a,r[b+6]=A+h,b+=8}u+=g,c+=g,m+=y,b+=y,p+=f}},t.prototype.getRenderError=function(){return this._error},t}(),P.AudioOutput.WebAudio=function(){var t=function(e){this.context=t.CachedContext=t.CachedContext||new(window.AudioContext||window.webkitAudioContext),this.gain=this.context.createGain(),this.destination=this.gain,this.gain.connect(this.context.destination),this.context._connections=(this.context._connections||0)+1,this.startTime=0,this.buffer=null,this.wallclockStartTime=0,this.volume=1,this.enabled=!0,this.unlocked=!t.NeedsUnlocking(),Object.defineProperty(this,"enqueuedTime",{get:this.getEnqueuedTime})};return t.prototype.destroy=function(){this.gain.disconnect(),this.context._connections--,0===this.context._connections&&(this.context.close(),t.CachedContext=null)},t.prototype.play=function(t,e,i){if(this.enabled){if(!this.unlocked){var r=P.Now();return this.wallclockStartTime<r&&(this.wallclockStartTime=r),void(this.wallclockStartTime+=e.length/t)}this.gain.gain.value=this.volume;var s=this.context.createBuffer(2,e.length,t);s.getChannelData(0).set(e),s.getChannelData(1).set(i);var o=this.context.createBufferSource();o.buffer=s,o.connect(this.destination);var n=this.context.currentTime,a=s.duration;this.startTime<n&&(this.startTime=n,this.wallclockStartTime=P.Now()),o.start(this.startTime),this.startTime+=a,this.wallclockStartTime+=a}},t.prototype.stop=function(){this.gain.gain.value=0},t.prototype.getEnqueuedTime=function(){return Math.max(this.wallclockStartTime-P.Now(),0)},t.prototype.resetEnqueuedTime=function(){this.startTime=this.context.currentTime,this.wallclockStartTime=P.Now()},t.prototype.unlock=function(t){if(this.unlocked)t&&t();else{this.unlockCallback=t;var e=this.context.createBuffer(1,1,22050),i=this.context.createBufferSource();i.buffer=e,i.connect(this.destination),i.start(0),setTimeout(this.checkIfUnlocked.bind(this,i,0),0)}},t.prototype.checkIfUnlocked=function(t,e){t.playbackState===t.PLAYING_STATE||t.playbackState===t.FINISHED_STATE?(this.unlocked=!0,this.unlockCallback&&(this.unlockCallback(),this.unlockCallback=null)):e<10&&setTimeout(this.checkIfUnlocked.bind(this,t,e+1),100)},t.NeedsUnlocking=function(){return/iPhone|iPad|iPod/i.test(navigator.userAgent)},t.IsSupported=function(){return window.AudioContext||window.webkitAudioContext},t.CachedContext=null,t}();document.currentScript.src;var I={start:0,playing:1,stop:2};s.prototype.resetPlayer=function(){if(this.player)try{this.player.stop(),this.player.destroy(),this.player=null}catch(t){this.logger.info("zp.rp.0 stop player destroy exception.")}this.state=I.stop,this.stateTimeStamp=Date.now(),this.playerStat={videoBytes:0,videoFrameCnt:0,videoDecodeFrameCnt:0}},s.prototype.newPlayer=function(t){return this.resetPlayer(),this.player=new P.Player(this.urls[this.playUrlIndex%this.urls.length],{audio:!1,canvas:this.view,viewMode:this.viewMode}),!!this.player&&(this.state=I.start,!0)},s.prototype.setVolume=function(t){if(this.logger.debug("zp.sv.0 call"),"number"!=typeof t||t<0||t>100)return this.logger.info("zp.sv.0 param error"),!1;this.player.volume(t/100),this.logger.debug("zp.sv.0 call success")},s.prototype.getCurrentPlayerUrl=function(){return this.urls[this.playUrlIndex%this.urls.length]},s.prototype.updatePlayerStat=function(){this.playerStat.videoBytes=this.player.playoutStatus.videoBytes,this.playerStat.videoFrameCnt=this.player.playoutStatus.videoFrameCnt,this.playerStat.videoDecodeFrameCnt=this.player.playoutStatus.videoDecodeFrameCnt},s.prototype.enableDecode=function(t){return this.logger.info("zp.ed.0 call"),"boolean"!=typeof t?(this.logger.info("zp.ed.0 param error"),!1):this.player?(this.decode=t,this.player.enableDecode(t),this.logger.info("zp.ed.0 call success "+t),!0):(this.logger.info("zp.ed.0 don't have player"),!1)};var U={start:0,playing:1,stop:2},F={start:0,stop:1,error:2},N={cdn:0,ultra:1};o.prototype.setPlaySourceType=function(t){this.sourceType=t},o.prototype.startPlayingStream=function(t,e,i,r,o,a){this.logger.debug("zpc.sps.0 call");var h=this.playerList[t];if(h||this.stopPlayingStream(t),!(h=this.playerList[t]=new s(this.logger,t,e,i,(this.sourceType,2),r,o,a)))return this.logger.info("zpc.sps.0 new player failed"),!1;++this.playerCount;var l=n(this,h);return this.logger.debug("zpc.sps.0 call result:",l),l},o.prototype.stopPlayingStream=function(t){this.logger.debug("zpc.sps.1.0 call");var e=this.playerList[t];e&&(e.resetPlayer(),P.DestroySingleRenderer(e.view.id),delete this.playerList[t],void 0!==this.playerInfo[t]&&delete this.playerInfo[t],--this.playerCount,this.onPlayStateUpdate(F.stop,e.streamid)),a(this),this.logger.debug("zpc.sps.1.0 call success")},o.prototype.setPlayVolume=function(t,e){var i=this.playerList[t];return i&&i.player?i.player.setVolume(e):(this.logger.info("zpc.spv.0 no player"),!1)},o.prototype.reset=function(){this.logger.debug("zpc.r.0 call");for(var t in this.playerList){var e=this.playerList[t];e&&e.resetPlayer()}this.playerCount=0,this.playerList={},this.playerInfo={},a(this),P.DestroyAllRenderer(),this.logger.debug("zpc.r.0 call success")},o.prototype.enableDecode=function(t,e){this.logger.debug("zpc.ed.0 call"),this.playerInfo[t]=e;var i=this.playerList[t];if(i)if(i.state==U.playing){var r=i.enableDecode(e);this.logger.info("zpc.ed.0 player is playing, streamId="+t+" set decode "+e+r)}else this.logger.info("zpc.ed.0 player is not start palying, streamId= "+t+" set decode "+e);else this.logger.info("zpc.ed.0 cannot find player")},o.prototype.onPlayStateUpdate=function(t,e){},o.prototype.onPlayQualityUpdate=function(t,e){};var B={cdn:0,ultra:1},M={logout:0,trylogin:1,login:2},O={added:0,deleted:1},q={added:12001,deleted:12002,updated:12003},z=5,V=[2e3,2e3,3e3,3e3,4e3],H=3,Y=3e3,Q="1.0.3";l.prototype.onPlayStateUpdateHandle=function(t,e){this.onPlayStateUpdate(t,e)},l.prototype.onPlayQualityUpdateHandle=function(t,e){this.onPlayQualityUpdate(t,e)},l.prototype.config=function(t){return this.logger.debug("zc.p.c.0 call"),this.appid=t.appid,this.server=t.server,this.idName=t.idName,this.nickName=t.nickName,this.logger.setLogLevel(t.logLevel),this.workerUrl=t.workerUrl,this.workerPlayerJSBlob=t.workerPlayerJSBlob,void 0!=t.remoteLogLevel?this.logger.setRemoteLogLevel(t.remoteLogLevel):this.logger.setRemoteLogLevel(0),this.logger.setSessionInfo(t.appid,"","",t.idName,""),this.logger.openLogServer(t.logUrl),this.configOK=!0,this.logger.debug("zc.p.c.0 call success"),!0},l.prototype.login=function(t,e,i,r,s){return this.logger.setSessionInfo(this.appid,t,"",this.idName,""),this.logger.info("zc.p.l.0 call:",t,i),this.configOK?(this.runState!==M.logout&&(this.logger.debug("zc.p.l.0 reset"),g(this,M.logout),S(this)),this.logger.debug("zc.p.l.0 begin"),g(this,M.trylogin),this.roomid=t,this.token=i,this.role=e,function(t,e,i){var r=function(){},s=function(){};i.success&&"function"==typeof i.success&&(r=i.success),i.error&&"function"==typeof i.error&&(s=i.error),t.callbackList[e+"SuccessCallback"]=r,t.callbackList[e+"ErrorCallback"]=s}(this,"login",{success:r,error:s}),w(this),_(this),this.logger.info("zc.p.l.0 call success"),!0):(this.logger.info("zc.p.l.0 param error"),!1)},l.prototype.logout=function(){return this.logger.debug("zc.p.l.1.0 call"),this.runState===M.logout?(this.logger.info("zc.p.l.1.0 at logout"),!1):(g(this,M.logout),S(this),this.logger.debug("zc.p.l.1.0 call success"),!0)},l.prototype.setUserStateUpdate=function(t){return this.logger.debug("zc.p.eusu.0 call"),"boolean"!=typeof t?(this.logger.info("zp.p.eusu.0 param error"),!1):(this.userStateUpdate=t,this.logger.debug("zc.p.eusu.0 call success "+t),!0)},l.prototype.sendCustomCommand=function(t,e,i,r){if(this.logger.debug("zc.p.scc.0 call"),this.runState!==M.login)return this.logger.info("zc.p.scc.0 state error"),!1;if(!(t&&t instanceof Array&&0!=t.length))return this.logger.info("zc.p.scc.0 dstMembers error"),!1;var s={dest_id_name:t,custom_msg:e};return m(this,"custommsg",s,i,r),this.logger.debug("zc.p.scc.0 call success"),!0},l.prototype.sendRoomMsg=function(t,e,i,r,s){if(this.logger.debug("srm.0 call"),this.runState===M.login){var o=Date.parse(new Date);if(this.sendRoomMsgTime>0&&this.sendRoomMsgTime+this.SendRoomMsgInterval>o)return this.logger.info("srm.0 freq error"),void(s&&s(W.FREQ_LIMITED,0,t,e,i));this.sendRoomMsgTime=o,this.logger.debug("srm.0 send fetch request");m(this,"im_chat",{msg_category:t,msg_type:e,msg_content:i},r,s),this.logger.debug("srm.0 call success")}else this.logger.info("srm.0 state error")},l.prototype.startPlayingStream=function(t,e,i,r){i=i||1,this.logger.debug("zc.p.sps.0 call");var s="z0";"1"===r&&(s="z1"),this.logger.info("zc.p.sps.0 quality:"+s+" "+r);for(var o=null,n=0;n<this.streamList.length;n++)if(this.streamList[n].stream_id===t){if(this.streamList[n].quality!==s)break;o=this.streamList[n].urls_ws||[],this.streamList[n].viewMode=i;break}if(console.log("streamUrls = ",o),!o||o.length<=0)return this.logger.debug("zc.p.sps.0 fetch stream url"),this.mapStreamDom[t]=e,this.mapStreamQuality[t]=s,function(t,e,i){t.logger.debug("fsu.0 call"),t.runState===M.login?(t.logger.debug("fsu.0 send fetch request"),f(t,"stream_url",{stream_ids:[e],quality:i}),t.logger.debug("fsu.0 call success")):t.logger.info("fsu.0 state error")}(this,t,s),!1;this.logger.debug("zc.p.sps.0 play"),d(this,t,e)},l.prototype.stopPlayingStream=function(t){return this.logger.debug("zc.p.sps.1.0 call"),t&&""!==t?(this.playerCenter.stopPlayingStream(t),this.logger.debug("zc.p.sps.1.0 call success"),!0):(this.logger.info("zc.p.sps.1.0 param error"),!1)},l.prototype.setPlayVolume=function(t,e){return this.logger.debug("zc.p.spv.0 call"),this.playerCenter.setPlayVolume(t,e),this.logger.debug("zc.p.spv.0 call success"),!0},l.prototype.setPreferPlaySourceType=function(t){return this.logger.debug("zc.p.sppst.0 call"),"number"!=typeof t||t!==B.cdn&&t!==B.ultra?(this.logger.info("zc.p.sppst.0 param error"),!1):(this.preferPlaySourceType=t,this.playerCenter.setPlaySourceType(t),this.logger.debug("zc.p.sppst.0 call success"),!0)},l.prototype.release=function(){this.logger.debug("zc.p.r.0 call"),g(this,M.logout),S(this),this.playerCenter.reset(),this.logger.stopLogServer(),this.logger.debug("zc.p.r.0 call success")},l.prototype.enableDecode=function(t,e){if(this.logger.debug("zc.p.ed.0 call"),this.runState!=M.login)return this.logger.info("zc.p.ed.0 don't login in"),!1;for(var i=!1,r=0;r<this.streamList.length;r++)if(this.streamList[r].stream_id===t){i=!0;break}return i?(this.playerCenter.enableDecode(t,e),this.logger.debug("zc.p.ed.0 call success"),!0):(this.logger.info("zc.p.ed.0 cannot find stream"),!1)};for(var W={SUCCESS:{code:"ZegoClient.Success",msg:"success."},PARAM:{code:"ZegoClient.Error.Param",msg:"input error."},HEARTBEAT_TIMEOUT:{code:"ZegoClient.Error.Timeout",msg:"heartbeat timeout."},LOGIN_TIMEOUT:{code:"ZegoClient.Error.Timeout",msg:"login timeout."},SEND_MSG_TIMEOUT:{code:"ZegoClient.Error.Timeout",msg:"send customsg timeout."},RESET_QUEUE:{code:"ZegoClient.Error.Timeout",msg:"msg waiting ack is clear when reset."},LOGIN_DISCONNECT:{code:"ZegoClient.Error.Network",msg:"network is broken and login fail."},KICK_OUT:{code:"ZegoClient.Error.Kickout",msg:"kickout reason="},UNKNOWN:{code:"ZegoClient.Error.Unknown",msg:"unknown error."},FREQ_LIMITED:{code:"ZegoClient.Error.requencyLimited",msg:"Frequency Limited."}},G=["onDisconnect","onKickOut","onRecvCustomCommand","onStreamUpdated","onStreamExtraInfoUpdated","onPlayStateUpdate","onRecvRoomMsg","onPlayQualityUpdate","onUserStateUpdate","onGetTotalUserList"],X=0;X<G.length;X++)l.prototype[G[X]]=function(){};return l});


/***/ })
],[18]);