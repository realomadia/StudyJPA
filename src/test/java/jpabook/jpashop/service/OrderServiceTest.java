package jpabook.jpashop.service;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.entity.Address;
import jpabook.jpashop.entity.Member;
import jpabook.jpashop.entity.Order;
import jpabook.jpashop.entity.OrderStatus;
import jpabook.jpashop.entity.item.Book;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@ExtendWith(SpringExtension.class)
class OrderServiceTest {

    @Autowired OrderService orderService;
    @Autowired MemberService memberService;
    @Autowired ItemService itemService;
    @Autowired OrderRepository orderRepository;
    @Autowired EntityManager em;

    @Test
    public void order() throws Exception{
        Member member = new Member();
        member.setAddress(new Address("부산","장림","123-111"));
        member.setName("황정민1");
        memberService.join(member);
        Book book = new Book();
        book.setName("부산 JPA");
        book.setPrice(10000);
        book.setStockQuantity(10);
        itemService.saveItem(book);
        int count = 8;
        Long orderId = orderService.order(member.getId(), book.getId(), count);
        Order getOrder = orderRepository.findOne(orderId);
        assertEquals(OrderStatus.ORDER, getOrder.getStatus(), "상품 주문시 상태는 ORDER");
    }

    @Test
    void cancelOrder() {
        Member member = new Member();
        member.setAddress(new Address("부산2","장림2","123-111"));
        member.setName("황정민2");
        memberService.join(member);
        Book book = new Book();
        book.setName("부산 JPA");
        book.setPrice(10000);
        book.setStockQuantity(10);
        itemService.saveItem(book);
        int count = 2;
            Long orderId = orderService.order(member.getId(), book.getId(), count);
            orderService.cancelOrder(orderId);
    }
    @Test
    void overStock() {
            Member member = new Member();
            member.setAddress(new Address("부산2","장림2","123-111"));
            member.setName("황정민3");
            memberService.join(member);
            Book book = new Book();
            book.setName("부산 JPA");
            book.setPrice(10000);
            book.setStockQuantity(10);
            itemService.saveItem(book);
            int count = 11;
            assertThrows(NotEnoughStockException.class, () -> {
                orderService.order(member.getId(), book.getId(), count);
            });
    }

}