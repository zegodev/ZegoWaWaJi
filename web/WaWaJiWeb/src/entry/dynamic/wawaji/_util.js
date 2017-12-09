// 工具函数

// 通过id获取元素
function getById(selecotr) {
    return document.getElementById(selecotr);
}
// 移除类名
function removeClass(dom, className) {
    // 仿jq写法
    var rclass = /[\t\r\n\f]/g,
        j, clazz, curValue, cur;
    var removeList = className.split(',');
    curValue = dom.className;
    cur = (' ' + curValue + ' ').replace(rclass, ' ');
    if (cur) {
        j = 0;
        while ((clazz = removeList[j++])) {
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
    addList.forEach(function(item) {
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
    getById,
    removeClass,
    addClass,
    hideElement,
    showElement,
    setLocal,
    getLocal,
    registerOperateHandler,
    removeAllEventHandler,
};