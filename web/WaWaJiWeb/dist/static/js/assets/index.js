webpackJsonp([2],[
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
/* 2 */,
/* 3 */,
/* 4 */,
/* 5 */,
/* 6 */,
/* 7 */,
/* 8 */,
/* 9 */,
/* 10 */,
/* 11 */,
/* 12 */,
/* 13 */,
/* 14 */
/***/ (function(module, exports) {

// removed by extract-text-webpack-plugin

/***/ }),
/* 15 */
/***/ (function(module, exports) {

// removed by extract-text-webpack-plugin

/***/ }),
/* 16 */
/***/ (function(module, exports, __webpack_require__) {

__webpack_require__(14);
__webpack_require__(15);

__webpack_require__(0);

// 通过id获取元素
function getById(selecotr) {
    return document.getElementById(selecotr);
}
// 移除类名
function removeClass(dom, className) {
    var reg = new RegExp(className, 'g');
    dom.className = dom.className.replace(reg, '');
}

// 最外层盒子
var $app = getById('app');
// 娃娃机列表的盒子
var $wawajiList = getById('wawaji-list');

// 注意，以下请求地址都需要跨域，
// 可接受的域名为  http://test.zego.im
// 开发者可以设置本机的hosts来跨域     127.0.0.1  test.zego.im
// 设置完成后，本地访问test.zego.im 来运行demo

var appid = 3265350344;
// 获取房间列表的接口地址
var roomUrl = 'https://liveroom' + appid + '-api.zego.im/demo/roomlist?appid=' + appid; // 第三版协议 该请求地址，需要跨域

window.onload = function () {
    var globalData,
        roomList = [],
        roomstr = '';
    var xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function () {
        if (xhr.readyState == 4) {
            if (xhr.status == 200) {
                // 成功分支
                globalData = JSON.parse(xhr.responseText);
                console.log('globalData = ', globalData);

                // Demo这边获取房间的接口返回的结果中，包含了一些不是娃娃机推来的信息，所以做了筛选策略，
                // 开发者可以根据自己的实际情况来决定，可以不筛选，直接获取所有的房间ID
                globalData && globalData.data.room_list.forEach(function (item) {
                    if (item.room_id.indexOf("WWJ_ZEGO") === 0 && item.stream_info && item.stream_info.length !== 0) {
                        roomList.push({
                            roomID: item.room_id,
                            roomName: item.room_name
                        });
                    }
                });

                if (roomList.length === 0) {
                    $wawajiList.innerHTML = '房间列表为空';
                    // 移除最外层盒子的none类名
                    removeClass($app, 'none');
                    return;
                }
                // 获取到筛选后的房间ID，循环生成DOM结构
                console.log('roomList = ', roomList);
                roomList.forEach(function (item) {
                    roomstr += '<li class="list-item">\n                                    <a href="/wawaji/index.html?rid=' + item.roomID + '" class="hd">\n                                        <i class="icon"></i>\n                                        <div class="ft-msg">\n                                            <div class="name playing">\u6E38\u620F\u4E2D</div>\n                                            <div class="money">18\u91D1\u5E01/\u6B21</div>\n                                        </div>\n                                    </a>\n                                    <h2 class="room-title">' + item.roomName + '</h2>\n                                </li>';
                });
                // 生成完成，放入父级
                $wawajiList.innerHTML = roomstr;

                // 移除最外层盒子的none类名
                removeClass($app, 'none');
            } else {
                // 失败分支


                // 移除最外层盒子的none类名
                removeClass($app, 'none');
                console.log('获取房间列表失败');
                alert('获取房间列表失败');
            }
        }
    };
    xhr.open('GET', roomUrl, true);
    xhr.send();
};

/***/ })
],[16]);