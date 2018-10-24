package com.hxf.seckill.web;

import com.hxf.seckill.dto.Exposer;
import com.hxf.seckill.dto.SeckillExecution;
import com.hxf.seckill.dto.SeckillResult;
import com.hxf.seckill.entity.Seckill;
import com.hxf.seckill.enums.SeckillStateEnum;
import com.hxf.seckill.exception.RepeatKillExecption;
import com.hxf.seckill.exception.SeckillCloseExecption;
import com.hxf.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * 前端请求接口
 * Created by Administrator on 2017/12/22.
 */
@Controller
@RequestMapping("/seckill")
public class SeckillController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillService seckillService;
    /*
    * 获取全部的秒杀商品
    * */
    @RequestMapping(value = "list", method = RequestMethod.GET)
    public String list(Model model) {
        model.addAttribute("list", seckillService.getSeckillList());
        return "list";
    }

    /**
     * 查看秒杀商品明细
     *
     * @param seckillId 秒杀商品id
     * @param model     返回数据
     * @return 查询成功返回model对象，否则跳转到list页面
     */
    @RequestMapping(value = "{seckillId}/detail", method = RequestMethod.GET)
    public String detail(@PathVariable("seckillId") Long seckillId, Model model) {
        if (seckillId == null) {
            return "redirect:/seckill/list";
        }
        Seckill seckill = seckillService.getById(seckillId);
        if (seckill == null) {
            return "forward:/seckill/list";
        }
        model.addAttribute("seckill", seckill);
        return "detail";
    }

    /**
     * 获取秒杀接口
     *
     * @param seckillId 秒杀id
     * @return 成功返回秒杀接口URL，否则但会错误信息
     */
    @RequestMapping(value = "{seckillId}/exposer", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public SeckillResult<Exposer> exposer(@PathVariable("seckillId") Long seckillId) {
        SeckillResult<Exposer> result = null;
        try {
            Exposer exposer = seckillService.exportSeckillUrl(seckillId);
            result = new SeckillResult<Exposer>(true, exposer);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result = new SeckillResult<Exposer>(false, e.getMessage());
        } finally {
            return result;
        }
    }

    /**
     * 执行秒杀
     * @param seckillId 秒杀商品id
     * @param userPhone  用户电话，可以为空
     * @param md5       MD5的URL
     * @return 执行成返回提示，否则返回错误提示
     */
    @RequestMapping(value = "{seckillId}/{md5}/execution", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public SeckillResult<SeckillExecution> userPhone(@PathVariable("seckillId") Long seckillId,
                                                     @CookieValue(value = "userPhone", required = false) Long userPhone,
                                                     @PathVariable("md5") String md5) {
        SeckillResult<SeckillExecution> result = null;
        SeckillExecution seckillExecution;
        if (userPhone == null) {
            return new SeckillResult<SeckillExecution>(false, "未注册");
        }
        try {
//            seckillExecution = seckillService.executeSeckill(seckillId, userPhone, md5,0);
              seckillExecution = seckillService.executeSeckillProcedure(seckillId, userPhone, md5,0);
            return new SeckillResult<SeckillExecution>(true, seckillExecution);
        } catch (SeckillCloseExecption e) {
            seckillExecution = new SeckillExecution(seckillId, SeckillStateEnum.END);
            return new SeckillResult<SeckillExecution>(true, seckillExecution);
        } catch (RepeatKillExecption e) {
            seckillExecution = new SeckillExecution(seckillId, SeckillStateEnum.REPEAT_KELL);
            return new SeckillResult<SeckillExecution>(true, seckillExecution);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            seckillExecution = new SeckillExecution(seckillId, SeckillStateEnum.INSERT_EROR);
            return new SeckillResult<SeckillExecution>(true, seckillExecution);
        }
    }

    @RequestMapping(value = "time/now", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public SeckillResult<Long> execute(Model model) {
        return new SeckillResult<Long>(true, new Date().getTime());

    }
}
