package com.recipehub.config;

import com.recipehub.model.Recipe;
import com.recipehub.model.User;
import com.recipehub.repository.RecipeRepository;
import com.recipehub.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * DataLoader initializes sample users and a rich catalog of 20 realistic recipes
 * across diverse categories (Indian, Chinese, Italian, Mexican, Desserts, Breakfast, Snacks, Beverages).
 *
 * Safe & Idempotent: checks existsByTitle() before saving so restarting the application
 * NEVER creates duplicate recipes or disturbs existing user data.
 */
@Component
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(UserRepository userRepository, RecipeRepository recipeRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.recipeRepository = recipeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // 1. Initialize default test users if not present
        User admin = userRepository.findByEmail("admin@recipehub.com").orElseGet(() -> {
            User u = new User("System Admin", "admin@recipehub.com", passwordEncoder.encode("admin123"), "ADMIN");
            return userRepository.save(u);
        });

        User chef = userRepository.findByEmail("chef@recipehub.com").orElseGet(() -> {
            User u = new User("Chef John", "chef@recipehub.com", passwordEncoder.encode("chef123"), "CONTRIBUTOR");
            return userRepository.save(u);
        });

        userRepository.findByEmail("alice@recipehub.com").orElseGet(() -> {
            User u = new User("Alice Explorer", "alice@recipehub.com", passwordEncoder.encode("alice123"), "EXPLORER");
            return userRepository.save(u);
        });

        Long chefId = chef.getId();

        // 2. Prepare complete list of 20 high-quality seed recipes
        List<Recipe> seedRecipes = new ArrayList<>();

        // --- Original 5 Recipes ---
        Recipe r1 = new Recipe(
                "Paneer Butter Masala",
                "Rich and creamy curry made with cottage cheese in tomato and butter gravy.",
                "Paneer (250g), Butter (2 tbsp), Tomatoes (4), Onions (2), Cream (1/4 cup), Spices (Garam masala, chili powder)",
                "1. Saute onions and blend with cooked tomatoes.\n2. Melt butter in pan and add puree.\n3. Add paneer cubes and simmer with cream.",
                "https://images.unsplash.com/photo-1631452180519-c014fe946bc7?auto=format&fit=crop&w=800&q=80",
                "Curry",
                "APPROVED",
                chefId,
                "Chef John"
        );
        r1.setAverageRating(4.8);
        seedRecipes.add(r1);

        Recipe r2 = new Recipe(
                "Veg Biryani",
                "Fragrant basmati rice cooked with fresh seasonal vegetables and aromatic whole spices.",
                "Basmati Rice (2 cups), Mixed Veggies (carrots, beans, peas), Fried Onions, Saffron Milk, Biryani Masala",
                "1. Parboil rice with whole spices.\n2. Cook veggies with yogurt and spices.\n3. Layer rice and vegetables, cover and dum cook for 20 mins.",
                "https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?auto=format&fit=crop&w=800&q=80",
                "Rice",
                "APPROVED",
                chefId,
                "Chef John"
        );
        r2.setAverageRating(4.9);
        seedRecipes.add(r2);

        Recipe r3 = new Recipe(
                "Pasta Alfredo",
                "Classic creamy Italian pasta tossed in a velvety parmesan and garlic white sauce.",
                "Fettuccine pasta (200g), Heavy cream (1 cup), Parmesan cheese (1/2 cup), Garlic (3 cloves), Butter, Black pepper",
                "1. Boil pasta until al dente.\n2. Melt butter, sauté minced garlic, and stir in cream.\n3. Add parmesan cheese and toss with cooked pasta.",
                "https://images.unsplash.com/photo-1645112411341-6c4fd023714a?auto=format&fit=crop&w=800&q=80",
                "Italian",
                "APPROVED",
                chefId,
                "Chef John"
        );
        r3.setAverageRating(4.5);
        seedRecipes.add(r3);

        Recipe r4 = new Recipe(
                "Chocolate Cake",
                "Moist, decadent chocolate sponge layered with rich dark chocolate fudge frosting.",
                "Flour (2 cups), Cocoa powder (3/4 cup), Sugar (2 cups), Baking powder, Eggs (2), Milk (1 cup), Melted Butter",
                "1. Whisk dry ingredients together.\n2. Beat eggs, milk, and melted butter, then combine.\n3. Bake at 180°C (350°F) for 35 mins. Frost with chocolate ganache.",
                "https://images.unsplash.com/photo-1578985545062-69928b1d9587?auto=format&fit=crop&w=800&q=80",
                "Dessert",
                "APPROVED",
                chefId,
                "Chef John"
        );
        r4.setAverageRating(5.0);
        seedRecipes.add(r4);

        Recipe r5 = new Recipe(
                "Paneer Tikka",
                "Cubes of paneer marinated in spiced yogurt, threaded on skewers and grilled till charred.",
                "Paneer (300g), Greek yogurt (1/2 cup), Bell peppers, Red onion, Chaat masala, Mustard oil, Lemon juice",
                "1. Mix yogurt with spices and mustard oil.\n2. Coat paneer cubes and veggies in marinade for 30 mins.\n3. Skewer and roast in oven or tawa until nicely charred.",
                "https://images.unsplash.com/photo-1599488615731-7e5c2823ff28?auto=format&fit=crop&w=800&q=80",
                "Appetizer",
                "APPROVED",
                chefId,
                "Chef John"
        );
        r5.setAverageRating(4.7);
        seedRecipes.add(r5);

        // --- 15 Additional Recipes ---

        // 6. Butter Chicken (Curry / Indian)
        Recipe r6 = new Recipe(
                "Butter Chicken",
                "Tender tandoori chicken pieces simmered in a mildly spiced, velvety tomato and butter gravy.",
                "Chicken (500g), Butter (3 tbsp), Cream (1/2 cup), Pureed Tomatoes (4), Ginger-Garlic paste, Garam Masala, Kasuri Methi",
                "1. Marinate chicken with yogurt, chili powder, and ginger-garlic paste for 1 hr.\n2. Sear chicken in butter till lightly charred.\n3. Simmer spiced tomato puree, stir in cream, butter, and kasuri methi.\n4. Add chicken and simmer for 10 minutes.",
                "https://images.unsplash.com/photo-1603894584373-5ac82b2ae398?auto=format&fit=crop&w=800&q=80",
                "Curry",
                "APPROVED",
                chefId,
                "Chef John"
        );
        r6.setAverageRating(4.9);
        seedRecipes.add(r6);

        // 7. Chicken Biryani (Rice / Indian)
        Recipe r7 = new Recipe(
                "Chicken Biryani",
                "Royal Hyderabadi style layered basmati rice and marinated chicken cooked with saffron and aromatic spices.",
                "Basmati rice (3 cups), Chicken (600g), Fried onions (1 cup), Yogurt (1 cup), Saffron milk, Mint & coriander, Biryani spices",
                "1. Marinate chicken with yogurt, mint, and spices.\n2. Boil soaked basmati rice with whole spices until 70% done.\n3. Layer chicken and rice in heavy-bottomed pot, drizzle saffron milk and fried onions.\n4. Seal pot and cook on dum for 25 minutes.",
                "https://images.unsplash.com/photo-1589302168068-964664d93dc0?auto=format&fit=crop&w=800&q=80",
                "Rice",
                "APPROVED",
                chefId,
                "Chef John"
        );
        r7.setAverageRating(5.0);
        seedRecipes.add(r7);

        // 8. Masala Dosa (Breakfast)
        Recipe r8 = new Recipe(
                "Masala Dosa",
                "Crispy golden fermented rice crepe stuffed with spiced mashed potato filling, served with coconut chutney.",
                "Dosa batter (3 cups), Potatoes (3 boiled & mashed), Mustard seeds, Curry leaves, Turmeric, Onions, Green chilies, Ghee",
                "1. Heat oil, temper mustard seeds and curry leaves, saute onions, green chilies, and turmeric.\n2. Mix in mashed potatoes and salt for the filling.\n3. Pour a ladle of batter on hot tawa and spread in circles.\n4. Drizzle ghee, spread potato filling in center, fold and serve crisp.",
                "https://images.unsplash.com/photo-1668236543090-82eba5ee5976?auto=format&fit=crop&w=800&q=80",
                "Breakfast",
                "APPROVED",
                chefId,
                "Chef John"
        );
        r8.setAverageRating(4.8);
        seedRecipes.add(r8);

        // 9. Chole Bhature (Breakfast / Indian)
        Recipe r9 = new Recipe(
                "Chole Bhature",
                "Spicy and tangy chickpea curry served with puffed, golden-fried leavened bread.",
                "Kabuli Chana/Chickpeas (2 cups), Tea bag, Onions, Tomatoes, Chole masala, All-purpose flour/Maida (2 cups), Yogurt, Oil",
                "1. Pressure cook soaked chickpeas with a black tea bag for deep color.\n2. Prepare gravy with sautéed onions, ginger, garlic, tomatoes, and chole masala.\n3. Knead flour with yogurt and rest 2 hrs, roll and deep fry puffed bhature.\n4. Simmer chole in gravy and serve hot with pickled onions.",
                "https://images.unsplash.com/photo-1626777552726-4a6b54c97e46?auto=format&fit=crop&w=800&q=80",
                "Breakfast",
                "APPROVED",
                chefId,
                "Chef John"
        );
        r9.setAverageRating(4.9);
        seedRecipes.add(r9);

        // 10. Rajma Chawal (Curry / Indian)
        Recipe r10 = new Recipe(
                "Rajma Chawal",
                "Comforting North Indian red kidney beans slow-cooked in thick spiced onion-tomato gravy with steamed basmati rice.",
                "Red Rajma/Kidney beans (2 cups), Onions (2), Pureed tomatoes (3), Ginger-garlic paste, Cumin seeds, Coriander powder, Basmati rice",
                "1. Soak rajma overnight and pressure cook until tender.\n2. Saute cumin, finely chopped onions, and ginger-garlic paste.\n3. Add tomato puree and spices, cook until oil separates.\n4. Add boiled rajma with water, mash a few beans to thicken gravy, and simmer.\n5. Serve over steaming hot basmati rice with ghee.",
                "https://images.unsplash.com/photo-1601050690597-df0568f70950?auto=format&fit=crop&w=800&q=80",
                "Curry",
                "APPROVED",
                chefId,
                "Chef John"
        );
        r10.setAverageRating(4.7);
        seedRecipes.add(r10);

        // 11. Palak Paneer (Curry / Indian)
        Recipe r11 = new Recipe(
                "Palak Paneer",
                "Fresh cottage cheese cubes cooked in a vibrant, spiced smooth spinach puree.",
                "Fresh spinach leaves (400g), Paneer cubes (250g), Garlic (6 cloves), Onions, Tomatoes, Green chilies, Cream (2 tbsp), Garam masala",
                "1. Blanch spinach in boiling water for 2 mins, shock in ice water, and blend smooth.\n2. Sauté chopped garlic and onions, add chopped tomatoes and spices.\n3. Stir in smooth spinach puree and simmer gently for 5 mins.\n4. Add paneer cubes, swirl in fresh cream, and serve warm.",
                "https://images.unsplash.com/photo-1610057099443-fde8c4d50f91?auto=format&fit=crop&w=800&q=80",
                "Curry",
                "APPROVED",
                chefId,
                "Chef John"
        );
        r11.setAverageRating(4.6);
        seedRecipes.add(r11);

        // 12. Aloo Paratha (Breakfast)
        Recipe r12 = new Recipe(
                "Aloo Paratha",
                "Golden whole wheat flatbread stuffed with spiced mashed potatoes, roasted with generous butter.",
                "Whole wheat flour (2 cups), Potatoes (3 boiled), Green chilies, Ajwain seeds, Amchur powder, Fresh coriander, White butter",
                "1. Knead soft dough from whole wheat flour and water.\n2. Mash boiled potatoes with chopped green chilies, ajwain, amchur, and coriander.\n3. Stuff potato mixture inside rolled dough ball, seal, and gently roll flat.\n4. Roast on hot tawa with generous ghee or butter until crispy golden spots appear.",
                "https://images.unsplash.com/photo-1626074353765-517a681e40be?auto=format&fit=crop&w=800&q=80",
                "Breakfast",
                "APPROVED",
                chefId,
                "Chef John"
        );
        r12.setAverageRating(4.8);
        seedRecipes.add(r12);

        // 13. Samosa (Snacks)
        Recipe r13 = new Recipe(
                "Samosa",
                "Crispy, flaky deep-fried pastry pyramids filled with spiced potato and green peas filling.",
                "All-purpose flour (2 cups), Ajwain seeds, Ghee, Potatoes (4 boiled & diced), Green peas (1/2 cup), Cumin, Chaat masala, Oil for frying",
                "1. Rub ghee into flour with ajwain and knead stiff dough.\n2. Saute cumin, ginger, diced potatoes, peas, and spices for the filling.\n3. Roll dough, cut in half, form a cone, fill with stuffing, and seal edges with water.\n4. Deep fry on low-to-medium heat until golden brown and crispy.",
                "https://images.unsplash.com/photo-1541544741938-0af808871cc0?auto=format&fit=crop&w=800&q=80",
                "Snacks",
                "APPROVED",
                chefId,
                "Chef John"
        );
        r13.setAverageRating(4.9);
        seedRecipes.add(r13);

        // 14. Pav Bhaji (Snacks)
        Recipe r14 = new Recipe(
                "Pav Bhaji",
                "Mashed mixed vegetable curry loaded with butter and special bhaji masala, served with soft toasted buttered rolls.",
                "Potatoes, Cauliflower, Green peas, Capsicum, Tomatoes, Pav bhaji masala, Butter (100g), Lemon, Soft Pav buns",
                "1. Boil and mash potatoes, cauliflower, and peas.\n2. On a large tawa, melt butter, sauté finely chopped onions, capsicum, and tomatoes.\n3. Add mashed vegetables, pav bhaji masala, and red chili powder, mash thoroughly with water.\n4. Simmer with a slab of butter and serve with toasted butter pav and lemon wedges.",
                "https://images.unsplash.com/photo-1626132647523-66f5bf380027?auto=format&fit=crop&w=800&q=80",
                "Snacks",
                "APPROVED",
                chefId,
                "Chef John"
        );
        r14.setAverageRating(4.9);
        seedRecipes.add(r14);

        // 15. Manchurian (Chinese)
        Recipe r15 = new Recipe(
                "Manchurian",
                "Crispy fried vegetable balls tossed in a spicy, sweet, and tangy Indo-Chinese garlic soy sauce.",
                "Shredded cabbage (1 cup), Carrots (1/2 cup), Corn flour, Soy sauce, Garlic (8 cloves), Spring onions, Green chilies, Chili sauce",
                "1. Mix shredded vegetables with corn flour, form small balls, and deep fry until crispy.\n2. In a wok, stir fry minced garlic, ginger, green chilies, and spring onion whites.\n3. Add soy sauce, chili sauce, vinegar, and cornstarch slurry to create gravy.\n4. Toss crispy veggie balls in sauce and garnish with spring onion greens.",
                "https://images.unsplash.com/photo-1585032226651-759b368d7246?auto=format&fit=crop&w=800&q=80",
                "Chinese",
                "APPROVED",
                chefId,
                "Chef John"
        );
        r15.setAverageRating(4.6);
        seedRecipes.add(r15);

        // 16. Margherita Pizza (Italian)
        Recipe r16 = new Recipe(
                "Margherita Pizza",
                "Classic Neapolitan pizza with San Marzano tomato sauce, fresh mozzarella cheese slices, and fresh basil leaves.",
                "Pizza dough (1 ball), Crushed tomato sauce (1/2 cup), Fresh Mozzarella cheese (150g), Fresh basil leaves, Extra virgin olive oil",
                "1. Stretch pizza dough into a 10-inch circle on baking parchment.\n2. Spread crushed seasoned tomato sauce evenly leaving crust edge.\n3. Tear fresh mozzarella slices and place across the pizza.\n4. Bake in preheated oven at maximum temperature (250°C) for 10-12 mins.\n5. Top with fresh basil and a drizzle of olive oil.",
                "https://images.unsplash.com/photo-1574071318508-1cdbab80d002?auto=format&fit=crop&w=800&q=80",
                "Italian",
                "APPROVED",
                chefId,
                "Chef John"
        );
        r16.setAverageRating(4.8);
        seedRecipes.add(r16);

        // 17. Tacos (Mexican)
        Recipe r17 = new Recipe(
                "Tacos",
                "Crispy corn tortilla shells filled with seasoned black beans, fresh pico de gallo salsa, guacamole, and shredded cheese.",
                "Taco shells (6), Black beans (1 can), Taco seasoning, Diced tomatoes, Onions, Jalapenos, Lime, Avocado guacamole, Cheddar cheese",
                "1. Warm taco shells in the oven until lightly crisp.\n2. Saute black beans with taco seasoning and cumin.\n3. Prepare fresh pico de gallo by tossing diced tomatoes, red onions, jalapenos, and lime juice.\n4. Assemble taco shells with seasoned beans, guacamole, salsa, and shredded cheddar.",
                "https://images.unsplash.com/photo-1551504734-5ee1c4a1479b?auto=format&fit=crop&w=800&q=80",
                "Mexican",
                "APPROVED",
                chefId,
                "Chef John"
        );
        r17.setAverageRating(4.7);
        seedRecipes.add(r17);

        // 18. Gulab Jamun (Dessert)
        Recipe r18 = new Recipe(
                "Gulab Jamun",
                "Soft, melt-in-the-mouth milk solid dumplings fried to golden brown and soaked in fragrant rose and cardamom sugar syrup.",
                "Khoya/Mawa (200g) or Milk powder, All-purpose flour (3 tbsp), Cardamom powder, Sugar (2 cups), Water (2 cups), Rose water, Ghee for frying",
                "1. Make sugar syrup by boiling sugar and water with crushed cardamom and rose water.\n2. Knead khoya and flour into smooth crack-free dough balls.\n3. Deep fry dough balls in low-medium heat ghee until uniformly golden brown.\n4. Transfer hot fried jamuns directly into warm sugar syrup and let soak for 2 hours.",
                "https://images.unsplash.com/photo-1596797038530-2c107229654b?auto=format&fit=crop&w=800&q=80",
                "Dessert",
                "APPROVED",
                chefId,
                "Chef John"
        );
        r18.setAverageRating(4.9);
        seedRecipes.add(r18);

        // 19. Cheesecake (Dessert)
        Recipe r19 = new Recipe(
                "Cheesecake",
                "Velvety New York style baked cheesecake with a buttery graham cracker crust and sweet strawberry coulis topping.",
                "Cream cheese (500g), Graham cracker crumbs (200g), Butter (1/3 cup), Sugar (1 cup), Eggs (3), Sour cream, Vanilla extract, Strawberries",
                "1. Mix cracker crumbs with melted butter, press into springform pan, and chill.\n2. Beat cream cheese with sugar until smooth, add eggs one at a time, sour cream, and vanilla.\n3. Pour over crust and bake in water bath at 160°C (325°F) for 55 mins.\n4. Chill in refrigerator overnight and top with fresh strawberry glaze.",
                "https://images.unsplash.com/photo-1533134242443-d4fd215305ad?auto=format&fit=crop&w=800&q=80",
                "Dessert",
                "APPROVED",
                chefId,
                "Chef John"
        );
        r19.setAverageRating(4.8);
        seedRecipes.add(r19);

        // 20. Mango Lassi (Beverages)
        Recipe r20 = new Recipe(
                "Mango Lassi",
                "Creamy, chilled yogurt smoothie blended with ripe sweet Alphonso mangoes and a touch of fragrant green cardamom.",
                "Ripe Mango pulp (1.5 cups), Plain Greek yogurt (1.5 cups), Cold milk (1/2 cup), Sugar (2 tbsp), Crushed cardamom, Crushed ice, Pistachios",
                "1. Peel and chop ripe mangoes or use canned alphonso pulp.\n2. Add mango pulp, thick yogurt, cold milk, sugar, and cardamom into a blender.\n3. Blend on high speed for 1-2 minutes until thick, frothy, and creamy.\n4. Pour into tall glasses over ice, garnish with chopped pistachios and saffron strands.",
                "https://images.unsplash.com/photo-1546173159-315724a31696?auto=format&fit=crop&w=800&q=80",
                "Beverages",
                "APPROVED",
                chefId,
                "Chef John"
        );
        r20.setAverageRating(4.9);
        seedRecipes.add(r20);

        // 3. Save each recipe safely only if it does not already exist
        int addedCount = 0;
        for (Recipe r : seedRecipes) {
            if (!recipeRepository.existsByTitle(r.getTitle())) {
                recipeRepository.save(r);
                addedCount++;
            }
        }

        if (addedCount > 0) {
            System.out.println(">>> [DataLoader] Successfully seeded " + addedCount + " new recipes into RecipeHub catalog!");
        } else {
            System.out.println(">>> [DataLoader] All " + seedRecipes.size() + " seed recipes already present in database.");
        }
    }
}
