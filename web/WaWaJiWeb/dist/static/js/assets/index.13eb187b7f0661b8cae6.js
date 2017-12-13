webpackJsonp([1],{

/***/ 15:
/***/ (function(module, exports, __webpack_require__) {

__webpack_require__(2);
__webpack_require__(3);

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

/***/ }),

/***/ 2:
/***/ (function(module, exports) {

// removed by extract-text-webpack-plugin

/***/ }),

/***/ 3:
/***/ (function(module, exports) {

// removed by extract-text-webpack-plugin

/***/ })

},[15]);