package kr.ac.jbnu.jhh.wsdteachingjhh;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/v1/items")
public class ItemController {

    private final Map<Long, ItemDto> store = new HashMap<>();

    private long sequence = 1L;

    private ResponseEntity<ErrorResponse> error(int statusCode, String message) // 공통 응답 오류
    {
        return ResponseEntity.status(statusCode).body(new ErrorResponse(statusCode, message));
    }

    private boolean isValid(String name, Integer price) // 상품 이름과 가격 검사
    {
        return (name != null && !name.isBlank() && price != null && price >= 0);
    }

    @GetMapping
    public List<ItemDto> getItems() // 아이템 전부 조회
    {
        return new ArrayList<>(store.values()); // store에 저장된 모든 아이템을 리스트로 변환해 반환
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getItem(@PathVariable("id") Long id)
    {
        ItemDto item = store.get(id); // store에서 전달받은 id에 해당하는 ItemDto 객체를 가져옴

        if(item == null) // 만약 item의 값에 아무것도 없다면
        {
            return error(404, "Not Found."); // 404 Not Found 반환
        }
        return ResponseEntity.ok(item); // item의 값이 있다면 200 반환
    }

    @PostMapping
    public ResponseEntity<?> createItem(@RequestBody ItemCreateRequest request) // 새로운 item 생성
    {
        if (!isValid(request.getName(), request.getPrice())) // 상품 이름과 가격 검사가 제대로 이뤄지지 않았다면
        {
            return error(400, "Bad Request"); // 400 Bad Request 반환
        }

        ItemDto item = new ItemDto(); //item 객체 생성

        item.setId(sequence++); // 현재 sequence값을 item 객체의 아이디로 설정하고 sequence를 1 증가
        item.setName(request.getName()); // 요청에 있는 이름을 item 객체에 넣기
        item.setPrice(request.getPrice()); // 요청에 있는 가격을 item 객체에 넣기

        store.put(item.getId(), item); // item의 id를 키로 사용해 store에 item 저장

        return ResponseEntity.status(201).body(item); // 201 Created 반환
    }

    @PostMapping("/batch")
    public ResponseEntity<?> createItems(@RequestBody List<ItemCreateRequest> requests) // 아이템 객체들을 생성
    {
        if (requests.isEmpty()) // requests가 비어있다면
        {
            return error(400, "Bad Request"); // 400 Bad Request 반환
        }

        for (ItemCreateRequest request : requests) // // requests에 들어있는 모든 객체를 순회
        {
            if (request == null || !isValid(request.getName(), request.getPrice())) // request가 없고 상품 이름과 가격 검사가 제대로 이뤄지지 않았다면
            {
                return error(400, "Bad Request"); // 400 Bad Request 반환
            }
        }

        List<ItemDto> items = new ArrayList<>(); // ItemDto를 저장할 빈 items 리스트 객체 생성

        for(ItemCreateRequest request : requests) // requests에 들어있는 모든 객체를 순회
        {
            ItemDto item = new ItemDto(); // item 객체 생성

            item.setId(sequence++); // 현재 sequence값을 item 객체의 아이디로 설정하고 sequence를 1 증가
            item.setName(request.getName()); // 요청에 있는 이름을 item 객체에 넣기
            item.setPrice(request.getPrice()); // 요청에 있는 가격을 item 객체에 넣기

            store.put(item.getId(), item); // item의 id를 키로 사용해 store에 item 저장
            items.add(item); // items 객체에 item 객체를 추가
        }

        return ResponseEntity.status(201).body(items); // 201 Created 반환
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateItem (@PathVariable("id") Long id, @RequestBody ItemUpdateRequest request) // 아이템 정보 변경
    {

        ItemDto item = store.get(id); // store에서 전달받은 id에 해당하는 ItemDto 객체를 가져옴

        if (item == null) // 해당 id의 아이템이 존제하지 않으면
        {
            return error(404, "Not Found"); // 404 Not Found 반환
        }

        if (!isValid(request.getName(), request.getPrice()))
        {
            return error(400, "Bad Request");
        }

        item.setName(request.getName()); // 요청에서 받은 이름으로 item 이름 변경
        item.setPrice(request.getPrice()); // 요청에서 받은 가격으로 item 가격 변경
        return ResponseEntity.ok(item); // 200 Ok 반환
    }

    @PutMapping("/{id}/price")
    public ResponseEntity<?> updatePrice (@PathVariable("id") Long id, @RequestBody ItemUpdateRequest request) // 가격만 업데이트
    {

        ItemDto item = store.get(id); // store에서 전달받은 id에 해당하는 ItemDto 객체를 가져옴

        if (item == null) // 해당 id의 아이템이 존제하지 않으면
        {
            return error(404, "Not Found"); // 404 Not Found 반환
        }
        if(request.getPrice() == null || request.getPrice() < 0) // 만약 가격이 입력되지 않았거나 음수라면
        {
            return error(400, "Bad Request"); // 400 Bad Request 반환
        }

        item.setPrice(request.getPrice()); // 요청에서 받은 가격으로 item 가격 변경
        return ResponseEntity.ok(item); // 200 Ok 반환
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteItem(@PathVariable("id") Long id) // 선택한 아이템 지우기
    {
        ItemDto removed = store.remove(id); // store에 들어있는 id에 해당하는 객체를 제거

        if (removed == null) // 지울 것이 없다면
        {
            return error(404, "Not Found"); // 404 Not Found 반환
        }
        return ResponseEntity.noContent().build(); // 204 No Content 반환
    }

    @DeleteMapping
    public ResponseEntity<ItemDto> deleteItems() // 모든 아이템 지우기
    {
        store.clear(); // store에 저장된 모든 아이템을 지우기
        return ResponseEntity.noContent().build(); // 204 No Content 반환
    }

    @GetMapping("/test/500")
    public ResponseEntity<?> testInternalError()
    {
        return error(500, "Internal Server Error");
    }

    @GetMapping("/test/503")
    public ResponseEntity<?> testUnavailable()
    {
        return error(503, "Service Temporarily Unavailable");
    }
}
