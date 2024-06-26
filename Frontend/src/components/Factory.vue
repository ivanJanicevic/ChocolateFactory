<template>
  <section class="py-5">
    <div class="container px-4 px-lg-5 my-5">
      <!-- Factories Table -->
      <div class="row gx-5 gx-lg-1 align-items-center mb-4">
        <div class="col-md-12">
          <h1 class="display-5 fw-bold mb-4">{{ title }}</h1>
          <div class="table-responsive">
            <table class="table table-hover">
              <thead class="table-dark">
                <tr>
                  <th scope="col">Logo firme</th>
                  <th scope="col">Naziv</th>
                  <th scope="col">Lokacija</th>
                  <th scope="col">Prosečna ocena</th>
                  <th scope="col"></th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="factory in sortedFactories" :key="factory.id">
                  <td>
                    <img :src="factory.pathToLogo" alt="Factory Logo" class="img-thumbnail" style="max-width: 80px;">
                  </td>
                  <td>{{ factory.name }}</td>
                  <td>{{ factory.location }}</td>
                  <td style="text-align: center;">{{ factory.rate }}</td>
                  <td>
                    <button @click="pregledajFabriku(factory)" class="btn btn-outline-primary btn-sm">
                      Pregledaj fabriku
                    </button>
                  </td>
                </tr>
                <tr v-if="sortedFactories.length === 0">
                  <td colspan="5" class="text-center">Nema rezultata pretrage</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>

      <!-- Search Bars -->
      <div class="row gx-5 gx-lg-1 align-items-center">
        <div class="col-md-3 mb-2">
          <input v-model="searchParams.factoryName" type="text" class="form-control" placeholder="Naziv fabrike" @input="filterFactories">
        </div>
        <div class="col-md-3 mb-2">
          <input v-model="searchParams.chocolateName" type="text" class="form-control" placeholder="Ime čokolade" @input="filterFactories">
        </div>
        <div class="col-md-3 mb-2">
          <input v-model="searchParams.location" type="text" class="form-control" placeholder="Lokacija" @input="filterFactories">
        </div>
        <div class="col-md-2 mb-2">
          <input v-model="searchParams.averageRating" type="number" class="form-control" placeholder="Prosečna ocena" @input="filterFactories">
        </div>
      </div>

      <div class="row gx-5 gx-lg-1 align-items-center mt-3">
        <div class="col-md-3 mb-2">
          <label>
            <input type="checkbox" v-model="chocolateCategoryFilters.regular" @change="updateChocolateCategoryFilters"> Regular
          </label>
          <label>
            <input type="checkbox" v-model="chocolateCategoryFilters.cooking" @change="updateChocolateCategoryFilters"> Cooking
          </label>
        </div>
        <div class="col-md-3 mb-2">
          <label>
            <input type="checkbox" v-model="chocolateTypeFilters.milk" @change="updateChocolateTypeFilters"> Milk
          </label>
          <label>
            <input type="checkbox" v-model="chocolateTypeFilters.dark" @change="updateChocolateTypeFilters"> Dark
          </label>
          <label>
            <input type="checkbox" v-model="chocolateTypeFilters.white" @change="updateChocolateTypeFilters"> White
          </label>
        </div>
        <div class="col-md-3 mb-2">
          <label>
            <input type="checkbox" v-model="searchParams.isOpen" @change="filterFactories"> Prikaži samo otvorene
          </label>
        </div>
        <div class="col-md-6 mb-2">
          <button @click="searchFactories" class="btn btn-primary w-100">
            Pretraži
          </button>
        </div>
      </div>

      <!-- Sort Options -->
      <div class="row gx-5 gx-lg-1 align-items-center mt-3">
        <div class="col-md-4 mb-2">
          <select v-model="sortParams.criterion" class="form-control">
            <option value="">Sortiraj po...</option>
            <option value="name">Naziv</option>
            <option value="location">Lokacija</option>
            <option value="rate">Prosečna ocena</option>
          </select>
        </div>
        <div class="col-md-4 mb-2">
          <select v-model="sortParams.ascending" class="form-control">
            <option value="true">Rastuće</option>
            <option value="false">Opadajuće</option>
          </select>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted, reactive, computed } from 'vue';
import axios from 'axios';
import { useRouter } from 'vue-router';

const router = useRouter();

const title = ref("Fabrike čokolade");

const searchParams = reactive({
  factoryName: '',
  chocolateName: '',
  location: '',
  averageRating: null,
  chocolateType: '',
  chocolateCategory: '',
  isOpen: false
});

const sortParams = reactive({
  criterion: '',
  ascending: 'true'
});

const factories = ref([]);
const filteredFactories = ref([]);

const chocolateTypeFilters = reactive({
  milk: false,
  dark: false,
  white: false
});

const chocolateCategoryFilters = reactive({
  regular: false,
  cooking: false
});

onMounted(async () => {
  await loadFactories();
});

const loadFactories = async () => {
  try {
    const response = await axios.get('http://localhost:8080/WebShopAppREST/rest/factories');
    factories.value = response.data;
    filterFactories();
  } catch (error) {
    console.error(error);
    title.value = "Greška u učitavanju";
  }
};

const filterFactories = () => {
  filteredFactories.value = factories.value.filter(factory => {
    const matchFactoryName = factory.name.toLowerCase().includes(searchParams.factoryName.toLowerCase());
    const matchChocolateName = factoryContainsChocolate(factory, searchParams.chocolateName);
    const matchLocation = factory.location.toLowerCase().includes(searchParams.location.toLowerCase());
    const matchAverageRating = !searchParams.averageRating || factory.rate >= searchParams.averageRating;
    const matchChocolateCategory =
      (!chocolateCategoryFilters.regular || factoryContainsChocolateWithCategory(factory, 'Regular')) &&
      (!chocolateCategoryFilters.cooking || factoryContainsChocolateWithCategory(factory, 'Cooking'));
    const matchIsOpen = !searchParams.isOpen || factory.status == "Work";
    const matchChocolateType =
      (!chocolateTypeFilters.milk || factoryContainsChocolateWithType(factory, 'Milk')) &&
      (!chocolateTypeFilters.dark || factoryContainsChocolateWithType(factory, 'Dark')) &&
      (!chocolateTypeFilters.white || factoryContainsChocolateWithType(factory, 'White'));

    return (
      matchFactoryName &&
      matchChocolateName &&
      matchLocation &&
      matchAverageRating &&
      matchChocolateCategory &&
      matchIsOpen &&
      matchChocolateType
    );
  });
};

const factoryContainsChocolate = (factory, chocolateName) => {
  // Implement logic to check if factory contains chocolate with given chocolateName
  return true; // Replace with actual implementation
};

const factoryContainsChocolateWithType = (factory, chocolateType) => {
  // Implement logic to check if factory contains chocolate with given chocolateType
  return true; // Replace with actual implementation
};

const factoryContainsChocolateWithCategory = (factory, chocolateCategory) => {
  // Implement logic to check if factory contains chocolate with given chocolateCategory
  return true; // Replace with actual implementation
};

const pregledajFabriku = (factory) => {
  router.push({ path: `/factories/${factory.id}` });
};

const searchFactories = async () => {
  try {
    const response = await axios.get('http://localhost:8080/WebShopAppREST/rest/factories/search', {
      params: {
        factoryName: searchParams.factoryName,
        chocolateName: searchParams.chocolateName,
        location: searchParams.location,
        averageRating: searchParams.averageRating,
        chocolateType: searchParams.chocolateType,
        chocolateCategory: searchParams.chocolateCategory,
        isOpen: searchParams.isOpen
      }
    });
    factories.value = response.data;
    filterFactories();
  } catch (error) {
    console.error(error);
    title.value = "Greška u pretrazi";
  }
};

const sortedFactories = computed(() => {
  let sorted = [...filteredFactories.value];
  if (sortParams.criterion) {
    sorted.sort((a, b) => {
      let modifier = sortParams.ascending === 'true' ? 1 : -1;
      if (a[sortParams.criterion] < b[sortParams.criterion]) return -1 * modifier;
      if (a[sortParams.criterion] > b[sortParams.criterion]) return 1 * modifier;
      return 0;
    });
  }
  return sorted;
});

const updateChocolateTypeFilters = () => {
  const selectedTypes = [];
  if (chocolateTypeFilters.milk) selectedTypes.push('Milk');
  if (chocolateTypeFilters.dark) selectedTypes.push('Dark');
  if (chocolateTypeFilters.white) selectedTypes.push('White');
  searchParams.chocolateType = selectedTypes.join(',');
  filterFactories();
};

const updateChocolateCategoryFilters = () => {
  const selectedCategories = [];
  if (chocolateCategoryFilters.regular) selectedCategories.push('Regular');
  if (chocolateCategoryFilters.cooking) selectedCategories.push('Cooking');
  searchParams.chocolateCategory = selectedCategories.join(',');
  filterFactories();
};
</script>

<style scoped>
.table {
  width: 100%;
  border-collapse: collapse;
}

.table th, .table td {
  padding: 0.75rem;
  vertical-align: top;
  border-top: 1px solid #dee2e6;
}

.table thead th {
  vertical-align: bottom;
  border-bottom: 2px solid #dee2e6;
}

.btn {
  font-size: 14px;
  padding: 0.4rem 1rem;
}

.btn-outline-primary {
  color: #007bff;
  border-color: #007bff;
}

.btn-outline-primary:hover {
  background-color: #007bff;
  color: #fff;
}

.img-thumbnail {
  max-width: 100%;
  height: auto;
  border: 0;
}

.text-center {
  text-align: center;
  width: 100%;
}

.display-5 {
  font-size: 2rem;
  font-weight: 500;
}

.form-control {
  height: calc(1.5em + 0.75rem + 2px);
  padding: 0.375rem 0.75rem;
  font-size: 0.875rem;
  line-height: 1.5;
  border-radius: 0.25rem;
  border: 1px solid #ced4da;
}

.form-control:focus {
  color: #495057;
  background-color: #fff;
  border-color: #80bdff;
  outline: 0;
  box-shadow: 0 0 0 0.25rem rgba(0, 123, 255, 0.25);
}

.mb-2 {
  margin-bottom: 0.5rem;
}
</style>
