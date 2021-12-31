package com.example.demo.schedule;

import com.example.demo.entity.Money;
import com.example.demo.entity.Order;
import com.example.demo.entity.Product;
import com.example.demo.service.ProductService;
import com.google.gson.Gson;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;
import java.util.UUID;

@Component
@EnableScheduling
public class Schedule implements InitializingBean {

    @Autowired
    private ProductService productService ;
    @Override
    public void afterPropertiesSet() throws Exception {
        processSellProduct();
    }

    private void processSellProduct(){
        Scanner in = new Scanner(System.in);
        System.out.println("===================== Wellcome ==========================");
        System.out.println("=========================================================");
        System.out.println("=========================================================");
        System.out.println("bạn có muốn mua hàng : (Yes(1)/No(0))");
        String cfstart = in.nextLine();
        if (cfstart.equals("Yes") || cfstart.equals("yes") || cfstart.equals("YES") || cfstart.equals("1")){
            System.out.println("Bạn đã yêu cầu mua hàng ");
            Order order = new Order();
            order.setId(UUID.randomUUID().toString());
            Integer totalRecharge = productService.getTotalUserRecharge();
            if (totalRecharge > 0 ){
                System.out.println("Số tiền trong tài khoản bạn sau khi nạp là :"+totalRecharge);
                order.setMoneyCharge(totalRecharge);
            }
            List<Product> lsProd = productService.getListProductToOder(totalRecharge);
            System.out.println("Giỏ hàng của hạn :");
            productService.showAllCart(lsProd);
            order.setLstProduct(lsProd);
            productService.paymentProduct(order);
            System.out.println("cảm ơn quý khách đã mua hàng ");
            processSellProduct();
        } else {
            processSellProduct();
        }
    }
}
