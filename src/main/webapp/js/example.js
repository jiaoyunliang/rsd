/**
 * Created by jf on 2015/9/11.
 * Modified by bear on 2016/9/7.
 */
function showTips() {

    $("#loginBtn").removeClass("weui-btn_loading");
    $("#loginBtn").html("登录");

    var $tooltips = $('.js_tooltips');
    if ($tooltips.css('display') != 'none') return;

    // toptips的fixed, 如果有`animation`, `position: fixed`不生效
    $('.page.cell').removeClass('slideIn');

    $tooltips.css('display', 'block');
    setTimeout(function () {
        $tooltips.css('display', 'none');
    }, 2000);

}
function quantityUnitText(quantity){
    var quantityText="";

    switch(quantity)
    {
        case 1:
            quantityText = "GJ";
            break;
        case 2:
            quantityText = "千瓦时";
            break;
        default:
            quantityText = "未知";
    }

    return quantityText;
}

function truckState(v) {
    var stateText = "";
    switch (v){
        case 1:
            stateText = "储热完成";
            break;
        case 2:
            stateText = "修理";
            break;
        case 3:
            stateText = "停运";
            break;
        case 4:
            stateText = "运送中";
            break;
        case 5:
            stateText = "储热中";
            break;
    }

    return stateText;
}

function stateText(val){
    var returnVal = "";
    if(val == 1){
        returnVal  = "取消";
    }else if(val == 2){
        returnVal  = "待接单";
    }else if(val == 3){
        returnVal  = "已接单";
    }else if(val == 4){
        returnVal = "到达";
    }else if(val == 5){
        returnVal = "开始供热";
    }else if(val == 6){
        returnVal = "待支付";
    }else if(val == 7){
        returnVal = "完成";
    }
    return returnVal;
}

//除法函数，用来得到精确的除法结果
//说明：javascript的除法结果会有误差，在两个浮点数相除的时候会比较明显。这个函数返回较为精确的除法结果。
//调用：accDiv(arg1,arg2)
//返回值：arg1除以arg2的精确结果
function accDiv(arg1,arg2){
    var t1=0,t2=0,r1,r2;
    try{t1=arg1.toString().split(".")[1].length}catch(e){}
    try{t2=arg2.toString().split(".")[1].length}catch(e){}
    with(Math){
        r1=Number(arg1.toString().replace(".",""));
        r2=Number(arg2.toString().replace(".",""));
        return (r1/r2)*pow(10,t2-t1);
    }
}
//给Number类型增加一个div方法，调用起来更加方便。
Number.prototype.div = function (arg){
    return accDiv(this, arg);
};
//乘法函数，用来得到精确的乘法结果
//说明：javascript的乘法结果会有误差，在两个浮点数相乘的时候会比较明显。这个函数返回较为精确的乘法结果。
//调用：accMul(arg1,arg2)
//返回值：arg1乘以arg2的精确结果
function accMul(arg1,arg2)
{
    var m=0,s1=arg1.toString(),s2=arg2.toString();
    try{m+=s1.split(".")[1].length}catch(e){}
    try{m+=s2.split(".")[1].length}catch(e){}
    return Number(s1.replace(".",""))*Number(s2.replace(".",""))/Math.pow(10,m);
}
//给Number类型增加一个mul方法，调用起来更加方便。
Number.prototype.mul = function (arg){
    return accMul(arg, this);
};
//加法函数，用来得到精确的加法结果
//说明：javascript的加法结果会有误差，在两个浮点数相加的时候会比较明显。这个函数返回较为精确的加法结果。
//调用：accAdd(arg1,arg2)
//返回值：arg1加上arg2的精确结果
function accAdd(arg1,arg2){
    var r1,r2,m;
    try{r1=arg1.toString().split(".")[1].length}catch(e){r1=0}
    try{r2=arg2.toString().split(".")[1].length}catch(e){r2=0}
    m=Math.pow(10,Math.max(r1,r2));
    return (arg1*m+arg2*m)/m;
}
//给Number类型增加一个add方法，调用起来更加方便。
Number.prototype.add = function (arg){
    return accAdd(arg,this);
}
//减法函数
function accSub(arg1,arg2){
    var r1,r2,m,n;
    try{r1=arg1.toString().split(".")[1].length}catch(e){r1=0}
    try{r2=arg2.toString().split(".")[1].length}catch(e){r2=0}
    m=Math.pow(10,Math.max(r1,r2));
    //last modify by deeka
    //动态控制精度长度
    n=(r1>=r2)?r1:r2;
    return ((arg2*m-arg1*m)/m).toFixed(n);
}
///给number类增加一个sub方法，调用起来更加方便
Number.prototype.sub = function (arg){
    return accSub(arg,this);
}


function getQueryString(name) {
    var reg = new RegExp('(^|&)' + name + '=([^&]*)(&|$)', 'i');
    var r = window.location.search.substr(1).match(reg);
    if (r != null) {
        return unescape(r[2]);
    }
    return null;
}

$.ajaxSettings = $.extend($.ajaxSettings, {
    ajaxSend : function (xhr, options) {
        
    },
    ajaxComplete : function (xhr, options) {

    }
});