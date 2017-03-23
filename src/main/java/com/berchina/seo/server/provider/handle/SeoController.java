package com.berchina.seo.server.provider.handle;

/**
 * @Package com.berchina.seo.server.provider.handle
 * @Description: TODO ( SEO 服务入口 )
 * @Author 任小斌  renxiaobin@berchin.com
 * @Date 16/8/30 上午11:31
 * @Version V1.0
 */
//@RestController
//@RequestMapping("/seo")
//public class SeoController {
//
//    @Autowired
//    private SeoServerFactory factory;
//
//    @RequestMapping(value = "/{channel}")
//    ResponseEntity<Response> seoGoods(HttpServletRequest request) {
//
//        return new ResponseEntity<>(factory.setSeoServer(new SeoRequest(request)), HttpStatus.OK);
//    }
//
//
//    @ApiOperation(value = "热词人工维护入口", notes = "若有疑问咨询开发人员（rxbyes）")
//    @ApiImplicitParams(
//            {@ApiImplicitParam(
//                    name = "hotword", value = "需要客户自动维护热词", required = true, dataType = "String")
//            })
//    @RequestMapping(value = "/goods/add/v1/", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
//    ResponseEntity<Object> add(@RequestParam(value = "hotword") String hotword) {
//
//        System.out.println(hotword);
//
//        Map<String, Object> map = Maps.newHashMap();
//
//        map.put("name", hotword);
//
//        return new ResponseEntity<>(map, HttpStatus.OK);
//    }
//}
