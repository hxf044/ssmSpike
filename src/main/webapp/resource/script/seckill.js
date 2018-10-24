/**
 * Created by Administrator on 2017/12/22.
 */
//存放主要交互逻辑的js代码
// javascript 模块化(package.类.方法)
var seckill = {

    URL: { //ajax请求URL封装
        now: function () {
            return "/Secondskill/seckill/time/now"
        },
        exposer: function ( seckillId ) {
            return "/Secondskill/seckill/" + seckillId + "/exposer"
        },
        execution: function ( seckillId, md5) {
            return "/Secondskill/seckill/" + seckillId + "/" + md5 + "/execution"
        }
    },
    validatePhone: function (phone) {  //手机号验证
        if (phone && phone.length == 11 && !isNaN(phone)) {
            return true
        } else {
            return false
        }
    },
    //详细页秒杀逻辑
    detail: {
        init: function (params) {
            var userPhon = $.cookie("userPhone")
            if (!seckill.validatePhone(userPhon)) {//如果验证不合格，提示
                //获取手机弹出框id
                var killPhoneModal = $("#killPhoneModal")
                //显示模态框(bootstrap)
                killPhoneModal.modal({
                    show: true,//显示弹出层
                    backdrop: 'static',//禁止位置关闭
                    keyboard: false//关闭键盘事件
                })

                $("#killPhoneBtn").click(function () {
                    var inputPhone = $("#killPhoneKey").val()
                    console.log("inputPhone:" + inputPhone)
                    if (seckill.validatePhone(inputPhone)) {
                        //电话写入cookie(7天过期)
                        $.cookie("userPhone", inputPhone, {expires: 7, path: "/Secondskill/seckill"})
                        //保存成功关闭弹出框
                        killPhoneModal.modal('hide')
                    } else {
                        //提示错误信息
                        $('#killPhoneMessage').hide().html('<label class="label label-danger">手机号错误!</label>').show(300);
                    }
                })
            }
            var startTime = params['startTime']
            var endTime = params['endTime']
            var seckillId = params['seckillId']
            $.get(seckill.URL.now(), {}, function (result) {
                if (result && result['success']) {
                    var nowTime = result['data'];
                    //时间判断 计时交互
                    seckill.countDown(seckillId, nowTime, startTime, endTime);
                } else {
                    console.log('result: ' + result);
                    alert('result: ' + result);
                }
            })

        }
    },
    //执行秒杀函数
    handlerSeckill: function (seckillId, node) {
        node.hide().html('<button class="btn btn-primary btn-lg" id="killBtn">开始秒杀</button>')
        $.get(seckill.URL.exposer(seckillId), {}, function (result) {
            if (result && result['success']) {
                var exposer = result['data'];
                if (exposer['exposed']) {
                    var md5 = exposer['md5']
                    var killUrl = seckill.URL.execution(seckillId, md5);
                    console.log("killUrl: " + killUrl);
                    //绑定一次点击事件
                    $("#killBtn").on('click', function () {
                        //执行秒杀请求
                        //1.先禁用按钮
                        $(this).addClass('disabled');//,<-$(this)===('#killBtn')->
                        $.post(killUrl, {}, function (result) {
                            if (result && result['success']) {
                                var killResult = result['data'];
                                var state = killResult['state'];
                                var stateInfo = killResult['stateInfo'];
                                //显示秒杀结果
                                node.html('<span class="label label-success">' + stateInfo + '</span>');
                            }
                        });
                    })
                    node.show();
                } else {
                    //未开启秒杀(浏览器计时偏差)
                    var now = exposer['now'];
                    var start = exposer['start'];
                    var end = exposer['end'];
                    seckill.countDown(seckillId, now, start, end);
                }
            } else {
                console.log('result: ' + result);
            }
        })
    },
    //倒计时函数
    countDown: function (seckillId, nowTime, startTime, endTime) {
        console.log(seckillId + '-' + nowTime + '-' + startTime + '-' + endTime);
        var seckillBox = $("#seckill-box")
        if (nowTime > endTime) {
            seckillBox.html("秒杀结束")
        } else if (nowTime < startTime) {
            //秒杀未开始,计时事件绑定
            var killTime = new Date(startTime + 1000);//todo 防止时间偏移
            seckillBox.countdown(killTime, function (event) {
                console.log("event:" + event)
                var format = event.strftime('秒杀倒计时: %D天 %H时 %M分 %S秒 ')
                seckillBox.html(format)
            }).on('finish.countdown', function () {
                //时间完成后回调事件
                //获取秒杀地址,控制现实逻辑,执行秒杀
                console.log('______fininsh.countdown');
                seckill.handlerSeckill(seckillId, seckillBox);
            })
        } else {
            //秒杀开始
            seckill.handlerSeckill(seckillId, seckillBox);
        }
    }
}
