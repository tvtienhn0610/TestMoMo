package com.example.demo.service;

import com.example.demo.entity.Money;
import com.example.demo.entity.Order;
import com.example.demo.entity.Product;
import com.example.demo.repository.MoneyRepository;
import com.example.demo.repository.ProductRepository;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.NumberUtils;
import org.springframework.util.SocketUtils;

import java.util.*;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MoneyRepository moneyRepository;

    public static boolean isNumeric(String str) {
        return str != null && str.matches("[-+]?\\d*\\.?\\d+");
    }

    public void showAllProduct() {
        List<Product> lProduct = productRepository.findAllByActive(1);
        if (!CollectionUtils.isEmpty(lProduct)) {
            System.out.println("chúng tôi có các mặt hàng sau:");
            System.out.println("Id sản phẩm | Tên sản phẩm | giá sản phẩm | số lượng");
            for (Product product : lProduct) {
                System.out.println(product.getId() + " | " + product.getName() + " | " + product.getPrice() + " | " + product.getNumber());
            }
        } else {
            System.out.println("Xin lỗi quý khách máy đã hết nước !!!!");
        }
    }

    public boolean paymentProduct(Order order) {
        Boolean checkPayment = false;
        Integer totalMoneyPayment = 0;
        Scanner in = new Scanner(System.in);
        try {
            System.out.println("Thanh toán nhập yes . để hủy nhập cancel");
            List<Product> listProd = order.getLstProduct();
            String cfPayment = in.nextLine();
            if (cfPayment.equals("Yes") || cfPayment.equals("yes") || cfPayment.equals("YES")|| cfPayment.equals("1")) {
                System.out.println("Thanh toán thành công ! vui lòng lấy sản phẩm và tiền thừa");
                System.out.println("Chi tiết hóa đơn :");
                System.out.println("Danh sách sản phẩm :");
                int i = 1;
                for (Product product : listProd) {
                    System.out.println(i + " | " + product.getName() + " | " + product.getPrice() + " | " + product.getQuantily() + " | " + (product.getPrice() * product.getQuantily()));
                    totalMoneyPayment = totalMoneyPayment + (product.getPrice() * product.getQuantily());
                    i++;
                }
                System.out.printf("Tổng tiền nap :" + order.getMoneyCharge());
                System.out.println("Tổng tiền cần thanh toán :" + totalMoneyPayment);
                order.setExcessMoney(order.getMoneyCharge() - totalMoneyPayment);
                System.out.println("Tiền thừa :" + order.getExcessMoney());
                System.out.printf("Chi tiết tiền thừa :");
                List<Money> listMoneyRefun = getlistMoneyRefun(order.getExcessMoney());
                for (Money money : listMoneyRefun){
                    if (money.getNumber() > 0){
                        System.out.println("Mệnh giá :"+money.getPrice()+" | số lượng :"+money.getNumber());
                    }
                }
                System.out.println("Số hàng còn trong máy :");
                showAllProduct();
            } else {
                System.out.println("Bạn đã hủy thanh toán . vui lòng nhận lại tiền");
                for (Product product : listProd) {
                    productRefund(product);
                }
                System.out.printf("Chi tiết tiền thừa :");
                List<Money> listMoneyRefun = getlistMoneyRefun(order.getMoneyCharge());
                for (Money money : listMoneyRefun){
                    if (money.getNumber() > 0){
                        System.out.println("Mệnh giá :"+money.getPrice()+"| số lượng :"+money.getNumber());
                    }
                }
                showAllProduct();
            }
        } catch (Exception e) {
            System.out.printf("Error payment :" + e);
        }
        return checkPayment;
    }

    public void productRefund(Product product) {
        try {
            Optional<Product> productOptional = productRepository.findById(product.getId());
            Product productnow = productOptional.get();
            Integer number = productnow.getNumber() + product.getQuantily();
            productnow.setNumber(number);
            productnow.setActive(1);
            productRepository.save(productnow);
        } catch (Exception e) {
            System.out.println("Error refund sản phẩm !!!");
        }
    }

    private void showAllMoney() {
        List<Money> lmoney = moneyRepository.findAll();
        if (!CollectionUtils.isEmpty(lmoney)) {
            System.out.println("cây đang có cái loại tiền như sau:");
            System.out.println("Id  | Tên | giá  | số lượng");
            for (Money money : lmoney) {
                System.out.println(money.getId() + " | " + money.getName() + " | " + money.getPrice() + " | " + money.getNumber());
            }
        } else {
            System.out.println("Xin lỗi quý khách máy đã hết nước !!!!");
        }
    }

    public Integer getTotalUserRecharge() {
        Integer totalRecharge = 0;
        Scanner in = new Scanner(System.in);
        try {
            while (true) {
                Integer recharge = getUserRecharge();
                if (recharge > 0) {
                    totalRecharge = totalRecharge + recharge;
                    System.out.println("Số tiền trong tài khoản của bạn là : " + totalRecharge);
                    System.out.println("Bạn có muốn nạp thêm nhập(Yes/No): ");
                    String cfcharge = in.nextLine();
                    if (cfcharge.equals("Yes") || cfcharge.equals("yes") || cfcharge.equals("YES") || cfcharge.equals("1")) {
                        continue;
                    }
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Error getTotalUserRecharge " + e);
        }
        return totalRecharge;
    }

    public Integer getUserRecharge() {
        Integer recharge = 0;
        Scanner in = new Scanner(System.in);
        try {
            System.out.println("Mời bạn nhập số tiền cần nạp :");
            String charge = in.nextLine();
            if (isNumeric(charge)) {
                recharge = Integer.parseInt(charge);
                Money money = moneyRepository.findByPriceAndActive(recharge, 1);
                if (money == null) {
                    System.out.println("Số tiền bạn nhập không hợp lệ .Nhập Yes để nhập lại Cancel để hủy");
                    String cfagain = in.nextLine();
                    if (cfagain.equals("Yes") || cfagain.equals("yes") || cfagain.equals("YES") || cfagain.equals("1")) {
                        recharge = getUserRecharge();
                    }
                } else {
                    Integer number = money.getNumber() + 1;
                    money.setNumber(number);
                    moneyRepository.save(money);
                    showAllMoney();
                }
            } else {
                System.out.println("Số tiền nạp không hợp lệ . Nhập Yes để nhập lại Cancel để hủy ");
                String cfagain = in.nextLine();
                if (cfagain.equals("Yes") || cfagain.equals("yes") || cfagain.equals("YES") || cfagain.equals("1")) {
                    recharge = getUserRecharge();
                }
            }
        } catch (Exception e) {
            System.out.println("Error recharge :" + e.getMessage());
        }
        return recharge;
    }

    public void showAllCart(List<Product> data) {
        try {
            System.out.println("Trong giỏ hàng bạn đang có :");
            System.out.println("Thứ tự |Tên hàng | số tiền | số lượng | tổng tiền");
            int i = 1;
            for (Product product : data) {
                System.out.println(i + " | " + product.getName() + " | " + product.getPrice() + " | " + product.getQuantily() + " | " + (product.getPrice() * product.getQuantily()));
                i++;
            }
        } catch (Exception e) {
            System.out.printf("Error show cart" + e);
        }
    }

    private Integer getTotalMoneyCart(List<Product> data, Product product) {
        Integer totalMoney = 0;
        try {
            for (Product prod : data) {
                totalMoney = totalMoney + ((prod.getQuantily() * prod.getPrice()));
            }
            totalMoney = totalMoney + ((product.getQuantily() * product.getPrice()));
        } catch (Exception e) {
            System.out.printf("Error show cart" + e);
        }
        return totalMoney;
    }

    private Integer getMoneyInWallet(List<Product> data, Integer totalCharnge) {
        Integer totalMoney = 0;
        try {
            for (Product prod : data) {
                totalMoney = totalMoney + ((prod.getQuantily() * prod.getPrice()));
            }
            totalMoney = totalCharnge - totalMoney;
        } catch (Exception e) {
            System.out.printf("Error show cart" + e);
        }
        return totalMoney;
    }

    public List<Product> getListProductToOder(Integer totalRerchage) {
        List<Product> listProduct = new ArrayList<>();
        Scanner in = new Scanner(System.in);
        try {
            while (true) {
                Product product = addNewProducttoOrder();
                if (product != null && product.getId() > 0) {
                    System.out.println("bạn vừa chọn sản phẩm =" + product.getName() + "|giá = " + product.getPrice() + "|số lương=" + "|" + product.getQuantily());
                    Integer totalMoneyBuy = getTotalMoneyCart(listProduct, product);
                    if (totalMoneyBuy <= totalRerchage) {
                        Product productany = listProduct.stream()
                                .filter(e -> e.getId().equals(product.getId())).findFirst().orElse(null);
                        if (productany != null) {
                            Integer totalquanly = productany.getQuantily() + product.getQuantily();
                            listProduct.remove(productany);
                            productany.setQuantily(totalquanly);
                            listProduct.add(productany);
                        } else {
                            listProduct.add(product);
                        }
                        showAllCart(listProduct);
                        System.out.println("số tiền còn lại trong ví :" + getMoneyInWallet(listProduct, totalRerchage));
                        System.out.println("bạn có muốn mua tiếp không (yes/no)");
                        String cfagain = in.nextLine();
                        if (cfagain.equals("Yes") || cfagain.equals("yes") || cfagain.equals("YES") || cfagain.equals("1")) {
                            continue;
                        }
                        break;
                    } else {
                        System.out.println("Số tiền còn lại không đủ để mua thêm sản phẩm.");
                        break;
                    }
                }
            }
        } catch (Exception e) {
            System.out.printf("Error getListProductToOder " + e.getMessage());
        }
        return listProduct;
    }

    public Product addNewProducttoOrder() {
        Product product = new Product();
        Scanner in = new Scanner(System.in);
        try {
            showAllProduct();
            System.out.println("Mời bạn lựa chọn sản phẩm (nhập Id sản phẩm).");
            String idProduct = in.nextLine();
            if (isNumeric(idProduct)) {
                System.out.println("test");
                product = productRepository.findByIdAndActive(Long.parseLong(idProduct), 1);
                if (product != null) {
                    System.out.println("Bạn vừa chọn sản phẩm =" + product.getName() + "|với giá =" + product.getPrice());
                    Integer quantily = getQuantilyProductFromUser(product);
                    if (quantily > 0) {
                        Integer numberPro = product.getNumber() - quantily;
                        product.setNumber(numberPro);
                        if (numberPro == 0) product.setActive(0);
                        productRepository.save(product);
                        product.setQuantily(quantily);
                    }
                } else {
                    System.out.println("Sản phẩm bạn chọn đã hết hoặc không tồn tại . nhập yes hoặc cancel để hủy chương trình  ");
                    String cfbuyagain = in.nextLine();
                    if (cfbuyagain.equals("Yes") || cfbuyagain.equals("yes") || cfbuyagain.equals("YES") || cfbuyagain.equals("1")) {
                        product = addNewProducttoOrder();
                    }
                }
            } else {
                System.out.println("Bạn nhập id không đúng định dạng hoặc không tồn tại .nhập yes hoặc cancel để hủy chương trình ");
                String cfbuyagain = in.nextLine();
                if (cfbuyagain.equals("Yes") || cfbuyagain.equals("yes") || cfbuyagain.equals("YES") || cfbuyagain.equals("1")) {
                    product = addNewProducttoOrder();
                }
            }
        } catch (Exception e) {
            System.out.println("Error addNewProducttoOrder" + e.getMessage());
        }
        return product;
    }

    private Integer getQuantilyProductFromUser(Product product) {
        Integer qua = 0;
        Scanner in = new Scanner(System.in);
        try {
            System.out.println("Nhập số lượng cần mua :");
            String quantily = in.nextLine();
            if (isNumeric(quantily) && (Integer.parseInt(quantily) <= product.getNumber())) {
                System.out.println("bạn đã chọn số lượng :" + quantily);
                qua = Integer.parseInt(quantily);
            } else {
                System.out.println("Số lượng bạn nhập sai định dạng hoặc quá số lượng hiện có. nhập yes để nhập lại hoặc cancel để hủy chương trình");
                String cfbuyagain = in.nextLine();
                if (cfbuyagain.equals("Yes") || cfbuyagain.equals("yes") || cfbuyagain.equals("YES") || cfbuyagain.equals("1")) {
                    qua = getQuantilyProductFromUser(product);
                }
            }
        } catch (Exception e) {
            System.out.println("Error getQuantilyProductFromUser " + e.getMessage());
        }
        return qua;
    }

    public List<Money> getlistMoneyRefun(Integer totalMoney) {
        List<Money> listMon = new ArrayList<>();
        List<Integer> listNum = new ArrayList<>();
        Integer s = 0;
        try {
            List<Money> listDb = moneyRepository.findAll();
            Collections.reverse(listDb);
            for (Money money : listDb) {
                if (totalMoney >= money.getPrice()) {
                    Integer count = totalMoney / money.getPrice();
                    if (count <= money.getNumber()) {
                        totalMoney = totalMoney % money.getPrice();
                        listNum.add(count);
                        Integer number = money.getNumber() - count ;
                        money.setNumber(number);
                        moneyRepository.save(money);
                        money.setNumber(count);
                        listMon.add(money);
                    } else {
                        listNum.add(0);
                        money.setNumber(0);
                        listMon.add(money);
                    }
                } else {
                    listNum.add(0);
                    money.setNumber(0);
                    listMon.add(money);
                }
            }
            if (totalMoney > 0) {
                System.out.println("máy không còn đủ tiền để trả lại.");
            }
        } catch (Exception e) {
            System.out.println("Error get money refun :" + e);
        }
        return listMon;
    }
}
