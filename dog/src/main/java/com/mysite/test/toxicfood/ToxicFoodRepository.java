package com.mysite.test.toxicfood;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ToxicFoodRepository extends JpaRepository<ToxicFood, Long> {
    Optional<ToxicFood> findByName(String name);
    boolean existsByName(String name);

//@Repository
//public class ToxicFoodRepository {
//    private static final Map<Long, ToxicFood> store = new HashMap<>();
//    private static final AtomicLong sequence = new AtomicLong(0L);
//
//    public ToxicFood create(ToxicFood food) {
//        if (food.getId() == null) {
//            food.setId(sequence.incrementAndGet());
//        }
//        store.put(food.getId(), food);
//        return food;
//    }
//
//    public Optional<ToxicFood> findById(Long id) {
//        return Optional.ofNullable(store.get(id));
//    }
//
//    public Optional<ToxicFood> findByName(String name) {
//        return store.values().stream()
//                .filter(f -> f.getName().equalsIgnoreCase(name))
//                .findFirst();
//    }
//
//    public List<ToxicFood> findAll() {
//        return new ArrayList<>(store.values());
//    }
//
//    public boolean existsById(Long id) {
//        return store.containsKey(id);
//    }
//
//    public boolean existsByName(String name) {
//        return store.values().stream()
//                .anyMatch(f -> f.getName().equalsIgnoreCase(name));
//    }
//
////	public List<ToxicFood> findByIngredient(ToxicIngredient ingredient) {
////		return store.values().stream().filter(f -> f.getIngredient() == ingredient).toList();
////	}
//
//	public void deleteById(Long id) {
//		store.remove(id);
//	}
}
