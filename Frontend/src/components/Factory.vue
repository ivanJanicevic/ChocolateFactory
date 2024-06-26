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
                <tr v-for="factory in filteredFactories" :key="factory.id">
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
                <tr v-if="filteredFactories.length === 0">
                  <td colspan="5" class="text-center">Nema rezultata pretrage</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>

      <!-- Search Bar -->
      <div class="row gx-5 gx-lg-1 align-items-center">
        <div class="col-md-3 mb-2">
          <input v-model="searchParams.factoryName" type="text" class="form-control" placeholder="Naziv fabrike">
        </div>
        <div class="col-md-3 mb-2">
          <input v-model="searchParams.chocolateName" type="text" class="form-control" placeholder="Ime čokolade">
        </div>
        <div class="col-md-3 mb-2">
          <input v-model="searchParams.location" type="text" class="form-control" placeholder="Lokacija">
        </div>
        <div class="col-md-2 mb-2">
          <input v-model="searchParams.averageRating" type="number" class="form-control" placeholder="Prosečna ocena">
        </div>
        <div class="col-md-1 mb-2">
          <button @click="searchFactories" class="btn btn-primary w-100">
            Pretraži
          </button>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue';
import axios from 'axios';
import { useRouter } from 'vue-router';

const router = useRouter();

const title = ref("Fabrike čokolade");

const searchParams = reactive({
  factoryName: '',
  chocolateName: '', // New property for chocolateName search
  location: '',
  averageRating: null
});

const factories = ref([]);
const filteredFactories = ref([]);

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
    const matchChocolateName = factoryContainsChocolate(factory, searchParams.chocolateName); // Call new function for chocolateName
    const matchLocation = factory.location.toLowerCase().includes(searchParams.location.toLowerCase());
    const matchAverageRating = !searchParams.averageRating || factory.rate >= searchParams.averageRating;

    return matchFactoryName && matchChocolateName && matchLocation && matchAverageRating;
  });
};

const factoryContainsChocolate = (factory, chocolateName) => {
  // Implement logic to check if factory contains chocolate with given chocolateName
  // This function should use factory.getChocolateIds() and check against the chocolates in your data
  // Example: return factory.getChocolateIds().includes(chocolateId);
  // You may need to adapt this based on your data structure
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
        chocolateName: searchParams.chocolateName, // Pass chocolateName to backend
        location: searchParams.location,
        averageRating: searchParams.averageRating
      }
    });
    factories.value = response.data;
    filterFactories();
  } catch (error) {
    console.error(error);
    title.value = "Greška u pretrazi";
  }
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
