<template>
  <div>
      <aside>
      </aside>
      <input type="checkbox" id="menu-toggle"/>
      <label id="trigger" for="menu-toggle"></label>
      <label id="burger" for="menu-toggle"></label>
      <ul id="menu">
        <li><a href="#" @click="factoryShowClick">Shopping</a></li>
        <li><a href="#" @click="purchasesDisplayClick">My purchases</a></li>
        <li><a href="#" @click="yourProfile">Your Profile</a></li>
        <li><a href="#" @click="logout">Log out</a></li>
      </ul>
    </div>
  <div v-if="shopping">
    <!-- Progress Bar -->
    <div class="progress-container">
      <div class="progress-step active">Odabir fabrike</div>
      <div class="progress-step" :class="{ active: isFactorySelected }">Odabir čokolade</div>
      <div class="progress-step" :class="{ active: cartShow }">Pregled i potvrda</div>
    </div>
    
    <!-- Conditional Rendering -->
    <div v-if="!isFactorySelected" class="factories-list-container">
  <!-- Factories List -->
  <div class="factories-list row row-cols-1 row-cols-md-2 row-cols-lg-4 g-4" style="margin-top: 10px;">
    <div class="col" v-for="factory in sortedFactories" :key="factory.id">
      <div class="card h-100" @click="selectFactory(factory)">
        <img :src="factory.pathToLogo" class="card-img-top img-thumbnail" alt="Factory Logo">
        <div class="card-body">
          <h5 class="card-title">{{ factory.name }}</h5>
          <p class="card-text location">Lokacija: {{ factory.location.address }}</p>
          <p class="card-text">Prosečna ocena: {{ factory.rate }}</p>
          <!-- Prikaz komentara za fabriku -->
          <div v-if="filteredComments(factory.id).length > 0">
            <h6 class="card-title">Komentari:</h6>
            <div v-for="comment in filteredComments(factory.id)" :key="comment.id">
              <p class="card-text"> - {{ comment.text }} </p>
            </div>
          </div>
          <p v-else class="card-text">Nema komentara za ovu fabriku.</p>
        </div>
      </div>
    </div>
  </div>
</div>
    
    <div v-if="chocolatesShow">
      <!-- Selected Factory Details and Chocolates -->
      <section class="py-5" style="width: 1200px;">
        <div class="row">
          <div class="col-md-30 d-flex align-items-center" style="margin-left: 0px; margin-bottom: 30px;">
            <button @click="factoryBack()" style="width: 160px; margin-bottom: 0px; margin-right: 300px; background-color: rgb(64, 151, 249); border-color: #ccc; font-weight: 800; font-size: 17px; letter-spacing: 1px;" class="btn btn-primary btn-add-to-cart">
              Izbor fabrike
            </button>
            <img :src="selectedFactory.pathToLogo" alt="Factory Logo" style="max-width: 100px;">
            <div>
              <h2 style="margin-bottom: 0px; margin-left: 5px; font-size: 35px; font-weight: bold; color: rgb(64, 151, 249);"> {{ selectedFactory.name }}</h2>
            </div>
            <button @click="pregledajKorpu()" style="width: 160px; margin-bottom: 0px; margin-left: 300px; background-color: rgb(64, 151, 249); border-color: #ccc; font-weight: 800; font-size: 17px; letter-spacing: 1px;" class="btn btn-primary btn-add-to-cart">
              Pregled Korpe
            </button>
          </div>

          <div class="col-md-30">
            <span v-if="!(chocolates && chocolates.length > 0)"> Nema dostupnih čokolada</span>
            <div class="row row-cols-1 row-cols-md-2 row-cols-lg-4 g-4" v-if="chocolates && chocolates.length > 0">
              <div class="col" v-for="chocolate in chocolates" :key="chocolate.id">
                <div class="card h-100">
                  <img :src="chocolate.picture" class="card-img-top img-thumbnail" alt="Slika čokolade">
                  <div class="card-body">
                    <h5 class="card-title">{{ chocolate.name }}</h5>
                    <p class="card-text">Masa: {{ chocolate.mass }}g</p>
                    <p class="card-text">Cena: {{ chocolate.cost }} RSD</p>
                    <div class="quantity-input">
                      <label for="quantity">Količina:</label>
                      <input type="number" v-model.number="chocolate.quantity" :max="chocolate.stock" min="1" @input="validateQuantity(chocolate)">
                    </div>
                    <button @click="addToCart(chocolate)" style="font-weight: 600; font-size: 16px; letter-spacing: 1px;" class="btn btn-primary btn-add-to-cart">Dodaj u korpu</button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>
    </div>
    
    <div v-if="cartShow" class="cart-section">
    <!-- Pregled Korpe -->
    <div style="display: flex; justify-content: space-between;">
      <button @click="izborCokolade()" style="width: 160px; margin-bottom: 10px; margin-right: 30px; background-color: rgb(64, 151, 249); border-color: #ccc; font-weight: 800; font-size: 17px; letter-spacing: 1px;" class="btn btn-primary btn-add-to-cart">Nazad</button>
        <h2 style="margin-left: 30px">Pregled Korpe</h2>
        <button @click="buy()" style="width: 160px; margin-bottom: 10px; margin-right: 30px; background-color: rgb(64, 151, 249); border-color: #ccc; font-weight: 800; font-size: 17px; letter-spacing: 1px;" class="btn btn-primary btn-add-to-cart">Poruči</button>
      </div>
    <div v-if="cart.chocolates.length > 0" class="cart-table-container" style="border: 3px solid #ccc;">
      <div class="container">
        <p class="discount">{{ discountString }}</p>
        <p class="total-price">Ukupna cena: {{ totalPrice * discount }} RSD</p>
      </div>
      <table class="custom-table">
        <thead>
          <tr>
            <th>Čokolada</th>
            <th>Količina</th>
            <th>Cena</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="chocolate in cart.chocolates" :key="chocolate.idChocolate">
            <td>{{ getChocolateName(chocolate.idChocolate) }}</td>
            <td>{{ chocolate.quantity }}</td>
            <td>{{ getChocolateCost(chocolate.idChocolate) * chocolate.quantity * discount }} RSD</td>
          </tr>
        </tbody>
      </table>
    </div>
    <div v-else>
      <p>Korpa je prazna.</p>
    </div>
  </div>
</div>

  <div v-if="profile">
    <div>
    <div>
      <div class="container rounded bg-white mt-5 mb-5">
        <div class="row justify-content-center">
          <div class="col-md-4 border-right">
            <div class="d-flex flex-column align-items-center text-center p-3 py-5">
              <img
                class="rounded-circle mt-5"
                width="150px"
                :src="user.profileImageUrl ? user.profileImageUrl : 'https://static.vecteezy.com/system/resources/previews/036/280/650/original/default-avatar-profile-icon-social-media-user-image-gray-avatar-icon-blank-profile-silhouette-illustration-vector.jpg'"
                alt="User Profile Image"
              />
              <span class="font-weight-bold">{{ user.username }}</span>
            </div>
          </div>
          <div class="col-md-5 border-right">
            <div class="p-3 py-5">
              <div class="d-flex justify-content-between align-items-center mb-3">
                <h4 class="text-right">Profile Settings</h4>
              </div>
              <div class="row mt-2">
                <div class="col-md-6">
                  <label class="labels">Name</label>
                  <input type="text" class="form-control" v-model="user.firstName" />
                </div>
                <div class="col-md-6">
                  <label class="labels">Surname</label>
                  <input type="text" class="form-control" v-model="user.lastName" />
                </div>
              </div>
              <div class="row mt-3">
                <div class="col-md-12">
                  <label class="labels">Username</label>
                  <input type="text" class="form-control" v-model="user.username" />
                </div>
                <div class="col-md-12">
                  <label class="labels">Gender</label>
                  <input type="text" class="form-control" v-model="user.gender" />
                </div>
                <div class="col-md-12">
                  <label class="labels">Birth date</label>
                  <input type="text" class="form-control" v-model="user.birthDate" />
                </div>
                <div class="col-md-12">
                  <label class="labels">Role</label>
                  <input type="text" class="form-control" v-model="user.role" />
                </div>
              </div>
              <div class="row mt-4">
                <div class="col-md-6">
                  <button class="btn btn-primary profile-button" type="button" @click="saveProfile">
                    Save Profile
                  </button>
                </div>
                <div class="col-md-6">
                  <button class="btn btn-primary profile-button" type="button" @click="logout">Logout</button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>

  <div v-if="purchasesShow" class="purchases-section">
    <div class="row gx-5 gx-lg-1 align-items-center mb-2">
    <div class="col-md-2 mb-2">
      <input v-model="searchParams.factoryName" type="text" class="form-control" placeholder="Naziv fabrike" @input="filterPurchases">
    </div>
    <div class="col-md-2 mb-2">
            <input v-model.number="searchParams.minPrice" type="number" class="form-control" placeholder="Min cijena" @input="filterPurchases">
        </div>
        <div class="col-md-2 mb-2">
            <input v-model.number="searchParams.maxPrice" type="number" class="form-control" placeholder="Max cijena" @input="filterPurchases">
        </div>
        <div class="col-md-2 mb-2">
          <input v-model="searchParams.startDate" type="date" class="form-control" placeholder="Datum od" @input="filterPurchases">
        </div>
        <div class="col-md-2 mb-2">
          <input v-model="searchParams.endDate" type="date" class="form-control" placeholder="Datum do" @input="filterPurchases">
        </div>
    <div class="col-md-2 mb-2">
      <select v-model="sortParams.criterion" class="form-select" aria-label="Default select example">
        <option value="">Sortiraj po...</option>
        <option value="factoryname">Naziv fabrike</option>
        <option value="price">Cena</option>
        <option value="date">Datum</option>
      </select>
    </div>
    <div class="col-md-2 mb-2">
      <select v-model="sortParams.ascending" class="form-select" aria-label="Default select example">
        <option value="true">Rastuće</option>
        <option value="false">Opadajuće</option>
      </select>
    </div>
  </div>
      <h2>Vaše kupovine</h2>
      <div class="table-responsive" style="min-height: 500px">
    <table class="table table-hover">
      <thead class="table-dark">
        <tr>
          <th scope="col">Naziv fabrike</th>
          <th scope="col">Cena</th>
          <th scope="col">Status</th>
          <th scope="col">Datum</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="purchase in sortedPurchases" :key="purchase.id">
          <td>{{ purchase.factory.name }}</td>
          <td>{{ purchase.price }}</td>
          <td>{{ purchase.status }}</td>
          <td>{{ purchase.purchaseDateTime }}</td>
          <td v-if="purchase.status === 'Obrada'"><button @click="cancelPurchase(purchase)" style="width: 160px; margin-bottom: 0px; margin-right: 30px; background-color: rgb(64, 151, 249); border-color: #ccc; font-weight: 800; font-size: 14px; letter-spacing: 1px;" class="btn btn-primary btn-add-to-cart">Otkazi</button></td>
          <td v-if="purchase.status === 'Odobreno'"><button @click="commentFactory(purchase)" style="width: 160px; margin-bottom: 0px; margin-right: 30px; background-color: rgb(64, 151, 249); border-color: #ccc; font-weight: 800; font-size: 14px; letter-spacing: 1px;" class="btn btn-primary btn-add-to-cart">Ostavi komentar</button></td>          
          <td else></td>
        </tr>
        <div v-if="showCommentModal" class="modal-overlay" @click.self="closeCommentModal">
            <div class="modal-content">
              <h3 style="margin-bottom: 15px; margin-left: 45px; font-size: 20px; font-weight: bold; color: rgb(64, 151, 249);">Ostavi komentar</h3>
              <textarea v-model="newComment" placeholder="Unesite vaš komentar ovde" style="width: 100%; height: 150px; padding: 10px; font-size: 16px; border: 1px solid #ccc; border-radius: 5px;"></textarea>
              <div>
              <label style="margin-top: 10px; margin-left: 0px; font-size: 16px; font-weight: bold; color: rgb(64, 151, 249);">Ocena:</label>
              <select v-model="ocena" style="width: 100px; margin-left: 10px; font-size: 16px; padding: 5px; border: 1px solid #ccc; border-radius: 5px;">
                  <option value="1">1</option>
                  <option value="2">2</option>
                  <option value="3">3</option>
                  <option value="4">4</option>
                  <option value="5">5</option>
              </select>
              </div>
              <button @click="submitComment" style="width: 200px; margin-bottom: 10px; margin-top: 10px; margin-left: 30px; background-color: rgb(64, 151, 249); border-color: #ccc; font-weight: 800; font-size: 17px; letter-spacing: 1px;" class="btn btn-primary btn-add-to-cart">Submit</button>
              <button @click="closeCommentModal" style="width: 160px; margin-bottom: 10px; margin-left: 50px; background-color: rgb(64, 151, 249); border-color: #ccc; font-weight: 800; font-size: 17px; letter-spacing: 1px;" class="btn btn-primary btn-add-to-cart">Close</button>
            </div>
          </div>
        <tr v-if="sortedPurchases.length === 0">
          <td colspan="5" class="text-center">Nema rezultata pretrage</td>
        </tr>
      </tbody>
    </table>
  </div>
    </div>
</template>

<script setup>
import { ref, onMounted, computed, reactive } from 'vue';
import axios from 'axios';
import { useRoute, useRouter } from 'vue-router';
import UserProfile from './UserProfile.vue'; // Prilagodite putanju prema stvarnoj strukturi vašeg projekta
import Factories from './Factory.vue'; // @ je alias za 'src' direktorij

const route = useRoute();
const router = useRouter();

const title = ref("Fabrike čokolade");

const currentComponent = ref(Factories); // Početno prikazivanje Factories komponente

const cartShow = ref(false);
const chocolatesShow = ref(false);
const factories = ref([]);
const filteredFactories = ref([]);
const selectedFactory = ref(null);
const chocolates = ref([]);
const chocolatesList = ref([]);
const comments = ref([]);
const newComment = ref();
const ocena = ref();
const isFactorySelected = ref(false);
const shopping = ref(true);
const profile = ref(false);
const showCommentModal = ref(false);
const userID = ref();
const discount = ref(1.0);
const discountString = ref();
onMounted(async () => {
  await loadFactories();
  await loadComments();
  this.userId = route.query.userId;
  this.userID = route.query.userId;
});

const yourProfile = () => {
  profile.value = true;
  purchasesShow.value = false;
  shopping.value = false;
  fetchUser();
};

const cancelPurchase = (purchase) => {
    console.log(`Cancelling purchase with id: ${purchase.id}`);
    axios.put(`http://localhost:8080/WebShopAppREST/rest/purchases/cancel/${purchase.id}`)
      .then(response => {
        purchase.status = 'Otkazano';
      })
      .catch(error => {
        console.error('Error cancelling purchase:', error);
      });
  };

  const submitComment = () => {
  axios.put(
    `http://localhost:8080/WebShopAppREST/rest/comments/${purchaseComment.value.factory.id}`,
    newComment.value,
    {
      headers: {
        'Content-Type': 'application/json'
      }
    }
  )
  .then(response => {
    console.log('Komentar uspješno dodan:', response.data);
    closeCommentModal();
  })
  .catch(error => {
    console.error('Greška pri dodavanju komentara:', error);
  });

  axios.put(
    `http://localhost:8080/WebShopAppREST/rest/factories/grade/${purchaseComment.value.factory.id}`,
    ocena.value,
    {
      headers: {
        'Content-Type': 'application/json'
      }
    }
  )
  .then(response => {
    console.log('Komentar uspješno dodan:', response.data);
    closeCommentModal();
  })
  .catch(error => {
    console.error('Greška pri dodavanju komentara:', error);
  });
};


const commentFactory = (purchase) => {
  showCommentModal.value = true;
  purchaseComment.value = purchase;
};


  const closeCommentModal = () => {
    showCommentModal.value = false;
  }

const purchasesShow = ref(false);
const purchases = ref([]);
const purchaseComment = ref(null);
const filteredPurchases = ref([]);

const searchParams = reactive({
  factoryName: '',
  minPrice: '',
  maxPrice: '',
  startDate: '',
  endDate: ''
});

const sortParams = reactive({
  criterion: '',
  ascending: 'true'
});

const filterPurchases = () => {
  filteredPurchases.value = purchases.value.filter(purchase => {
    const matchFactoryName = purchase.factory.name.toLowerCase().includes(searchParams.factoryName.toLowerCase());
    const matchPrice = (!searchParams.minPrice || purchase.price >= searchParams.minPrice) &&
                      (!searchParams.maxPrice || purchase.price <= searchParams.maxPrice);
    
    let startDateMatch = true;
    let endDateMatch = true;

    if (searchParams.startDate) {
      const startDate = new Date(searchParams.startDate);
      startDateMatch = new Date(purchase.purchaseDateTime) >= startDate;
    }

    if (searchParams.endDate) {
      const endDate = new Date(searchParams.endDate);
      endDateMatch = new Date(purchase.purchaseDateTime) <= endDate;
    }

    return (
      matchFactoryName &&
      matchPrice &&
      startDateMatch &&
      endDateMatch
    );
  });
};

const sortedPurchases = computed(() => {
  let sorted = [...filteredPurchases.value];
  if (sortParams.criterion) {
    sorted.sort((a, b) => {
      let modifier = sortParams.ascending === 'true' ? 1 : -1;
      if (sortParams.criterion === 'factoryname') {
        // Sortiranje po nazivu fabrike
        return modifier * (a.factory.name.localeCompare(b.factory.name));
      } else if (sortParams.criterion === 'price') {
        // Sortiranje po cijeni
        return modifier * (a.price - b.price);
      } else if (sortParams.criterion === 'date') {
        // Sortiranje po datumu
        let dateA = new Date(a.purchaseDateTime).getTime();
        let dateB = new Date(b.purchaseDateTime).getTime();
        return modifier * (dateA - dateB);
      }
      return 0;
    });
  }
  return sorted;
});

const purchasesDisplayClick = () => {
  loadPurchases();
  purchasesShow.value = true;
  shopping.value = false;
  profile.value = false;
  console.log("purchasesShow:", purchasesShow.value);
};

const factoryShowClick = () => {
  shopping.value = true;
  purchasesShow.value = false;
  profile.value = false;
}

const factoryBack = () => {
  chocolatesShow.value = false;
  shopping.value = true;
  isFactorySelected.value = false;
  factoryShowClick();
}

const izborCokolade = () => {
  chocolatesShow.value = true;
  cartShow.value = false;
  isFactorySelected.value = true;
}

const loadPurchases = async () => {
  try {
    const userId = route.query.id;
    const response = await axios.get(`http://localhost:8080/WebShopAppREST/rest/purchases/${userId}`);
    purchases.value = response.data;

    // Dohvati nazive fabrika za svaku kupovinu
    for (let purchase of purchases.value) {
      const factoryId = purchase.factory.id;
      const factoryResponse = await axios.get(`http://localhost:8080/WebShopAppREST/rest/factories/${factoryId}`);
      purchase.factory.name = factoryResponse.data.name; // Dodaj naziv fabrike u objekat kupovine
    }
    filterPurchases();
  } catch (error) {
    console.error(error);
  }
};

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

const loadComments = async () => {
  try {
    const response = await axios.get('http://localhost:8080/WebShopAppREST/rest/comments/valid');
    comments.value = response.data;
    console.log(comments);
  } catch (error) {
    console.error(error);
    title.value = "Greška u učitavanju";
  }
};

const filterFactories = () => {
  filteredFactories.value = factories.value.filter(factory => {
    return true; // Placeholder
  });
};

const filteredComments = (factoryId) => {
  return comments.value.filter(comment => comment.factoryId === factoryId);
};

const pregledajKorpu = () => {
      cartShow.value = true;
      chocolatesShow.value = false;
      console.log(user.value.type);
      console.log(discount.value);

      if (user.value.type === "Silver") {
        discount.value = 0.95;
        discountString.value = "Ostvarili ste 5% popusta kao Silver korisnik"
      } else if (user.value.type === "Golden") {
        discount.value = 0.9;
        discountString.value = "Ostvarili ste 10% popusta kao Golden korisnik"
      } else {
        discount.value = 1.0;
      }
      console.log(discount.value);
    };

const selectFactory = async (factory) => {
  selectedFactory.value = factory;
  isFactorySelected.value = true;
  chocolatesShow.value = true;
  await loadFactoryDetails(factory.id);
};

const getChocolateName = (id) => {
  const chocolate = chocolates.value.find(choco => choco.id === id);
  return chocolate ? chocolate.name : 'Nepoznata čokolada';
};

const getChocolateCost = (id) => {
  const chocolate = chocolates.value.find(choco => choco.id === id);
  return chocolate ? chocolate.cost : 0;
};

const loadFactoryDetails = async (factoryId) => {
  try {
    const chocolatesResponse = await axios.get(`http://localhost:8080/WebShopAppREST/rest/chocolates/factory/${factoryId}`);
    chocolates.value = chocolatesResponse.data.map(chocolate => ({ ...chocolate, quantity: 1 }));
    chocolatesList.value = chocolatesResponse.data;
    const commentsResponse = await axios.get(`http://localhost:8080/WebShopAppREST/rest/comments/factory/${factoryId}`);
    comments.value = commentsResponse.data;
  } catch (error) {
    console.error(error);
  }
};

const buy = async () => {
  console.log("PRE:" + cart.value.totalPrice)
  cart.value.totalPrice *= discount.value;
  console.log("POSLE:" + cart.value.totalPrice);
  try {
    const response = await axios.post(`http://localhost:8080/WebShopAppREST/rest/carts/`, cart.value);
    console.log(selectedFactory.value);
    const chocolatesResponse = await axios.get(`http://localhost:8080/WebShopAppREST/rest/chocolates/factory/${selectedFactory.value.id}`);
    chocolates.value = chocolatesResponse.data.map(chocolate => ({ ...chocolate, quantity: 1 }));
    chocolatesList.value = chocolatesResponse.data;
  } catch(error) {
    console.error(error);
  }
  
};
const sortedFactories = computed(() => {
  return filteredFactories.value;
});

const validateQuantity = (chocolate) => {
  if (chocolate.quantity > chocolate.stock) {
    chocolate.quantity = chocolate.stock;
  }
  if (chocolate.quantity < 1) {
    chocolate.quantity = 1;
  }
};

const userId = ref(""); // Postaviti odgovarajući userId
const cart = ref({
  id: "",
  userId: route.params.id,
  totalPrice: "",
  chocolates: []
});
const totalPrice = ref(0); // Implementacija prema potrebi

const addToCart = (chocolate) => {
  const priceToAdd = chocolate.cost * chocolate.quantity;
  totalPrice.value += priceToAdd;
  cart.value.totalPrice = totalPrice.value;

  // Provera da li čokolada već postoji u korpi
  const existingChocolate = cart.value.chocolates.find(item => item.idChocolate === chocolate.id);
  if (existingChocolate) {
    existingChocolate.quantity += chocolate.quantity;
  } else {
    cart.value.chocolates.push({ idChocolate: chocolate.id, quantity: chocolate.quantity });
  }
  console.log("Cart userID:", cart.value.userId);
  console.log("Cart price:", cart.value.price);
  console.log("Cart:", cart.value.userId);
  console.log("Cart choco:", cart.value.chocolates);
};

  // PROFIL //

  const user = ref({
  id: "",
  firstName: "",
  lastName: "",
  username: "",
  password: "",
  gender: "",
  birthDate: "",
  role: "",
  isActive: "",
  status: "",
  profileImageUrl: "",
  type: ""
}); // Definicija na globalnoj razini

const fetchUser = () => {
  const userId = route.params.id; // Pretpostavlja se da se koristi ID iz rute
  axios
    .get(`http://localhost:8080/WebShopAppREST/rest/user/profile/${userId}`)
    .then(response => {
      user.value = response.data; // Postavljanje vrijednosti korisnika
      console.log('Pronađen korisnik:', user);
    })
    .catch(error => {
      console.error('Greška prilikom dohvaćanja korisnika:', error);
    });
};


const saveProfile = async () => {
  console.log('Ažuriranje korisnika...');
  try {
    const response = await axios.put(`http://localhost:8080/WebShopAppREST/rest/user/update/${user.value.id}`, user.value);
    console.log('Korisnik uspješno ažuriran');
    localStorage.setItem('user', JSON.stringify(user.value)); // Ažurirajte lokalno skladište s novim podacima korisnika
  } catch (error) {
    console.error('Došlo je do greške:', error);
  }
};

const logout = () => {
  localStorage.removeItem('user');
  router.push('/login');
};

// OTKAZ

</script>

<style scoped>
.container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%; /* Adjust this value if necessary */
}

.discount {
  font-weight: 600;
  font-size: 19px;
}

.total-price {
  font-weight: 600;
  font-size: 19px;
  margin-left: auto;
}
.progress-container {
  display: flex;
  justify-content: space-around;
  margin-bottom: 20px;
}

.progress-step {
  flex: 1;
  padding: 10px;
  text-align: center;
  border-bottom: 3px solid lightgray;
}

.progress-step.active {
  font-weight: bold;
  border-bottom: 4px solid green;
  font-size: 20px;
}

.card {
  background-color: rgb(64, 151, 249);
  width: 100%;
  cursor: pointer;
  transition: transform 0.2s ease;
}

.card:hover {
  transform: scale(1.05);
}

.card-img-left {
  max-width: 150px;
  object-fit: cover;
  float: left;
  margin-right: 10px;
}

.card-title {
  color: white;
  font-weight: bold;
  margin-bottom: 1rem; /* Povećan razmak ispod naslova */
}

.card-text {
  color: white;
  font-weight: bold;
}

.card-text.location {
  margin-bottom: 0.5rem; /* Smanjen razmak između lokacije i prosečne ocene */
}

.border {
  border: 1px solid #dee2e6;
}

.custom-table {
  width: 100%;
  margin-bottom: 1rem;
  background-color: transparent;
  border-collapse: collapse;
}

.custom-table th,
.custom-table td {
  padding: 0.75rem;
  vertical-align: top;
  border-top: 1px solid #dee2e6;
}

.custom-table thead th {
  vertical-align: bottom;
  border-bottom: 2px solid #dee2e6;
}

.custom-table tbody + tbody {
  border-top: 2px solid #dee2e6;
}

.quantity-input {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.quantity-input label {
  margin-right: 10px;
  color: white;
}

.quantity-input input {
  width: 60px;
}

.cart-section {
  width: 1200px; /* Isto kao i kod drugih sekcija */
  margin-top: 20px; /* Dodaj margine po potrebi */
}

.cart-table-container {
  background-color: #f8f9fa; /* Boja pozadine, možeš promijeniti po želji */
  padding: 20px; /* Dodaj padding za bolji izgled */
  border-radius: 10px; /* Dodaj obrubljenje za bolji izgled */
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); /* Dodaj sjenu za bolji izgled */
}
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
}

.modal-content {
  background: white;
  padding: 20px;
  border-radius: 5px;
  width: 300px;
  max-width: 100%;
}


/* @import url(https://fonts.googleapis.com/css?family=Roboto:400,700); */

@keyframes checked-anim {
  50% {
    width: 300px; /* Smanjite širinu animacije */
    height: 450px; /* Smanjite visinu animacije */
  }
  100% {
    width: 250px; /* Konačna širina menija */
    height: 400px; /* Konačna visina menija */
    border-bottom-right-radius:20%;
  }
}
@keyframes not-checked-anim {
    0% {
        width: 3000px;
        height: 3000px;
    }
}
li, a {
    margin: 75px 0 -55px 0;
    color: #fff;
    font: 14pt "Roboto", sans-serif;
    font-weight: 700;
    line-height: 1.8;
    text-decoration: none;
    text-transform: none;
    list-style: none;
    outline: 0;
    display: none;
}
li {
    width: 230px;
    text-indent: 56px;}
a:focus {
    display: block;
    color: #333;
    background-color: #eee;
    transition: all .5s;
}
aside {
    position: absolute;
    color: white;
    top: 35%;
    right: 10%;
    text-align: right;
}
h1 {
    line-height: 0;
    font-size: 4vw;
    font-weight: 700;
}
h3 {
    float: right;
    line-height: .3;
    font-size: 2.5vw;
    font-weight: lighter;
}
h4 {
    float: left;
    margin-left: -2%;
    font-size: 1.5vw;
    font-weight: lighter;
}

html, body, template{
    margin: 0;
    padding: 0;
    height: 100%;
    background-color: #fff;
    font-family: 'Roboto', sans-serif;
    overflow: hidden;
}

#trigger, #burger, #burger:before, #burger:after {
    position: absolute;
    top: 25px;
    left: 25px; /* Align the right edge */
    background: #fff;
    width: 30px;
    height: 5px;
    transition: .2s ease;
    cursor: pointer;
    z-index: 1;
}

#trigger {
    height: 25px;
    background: none;
}
#burger:before {
    content: " ";
    top: 10px;
    left: 0;
}
#burger:after {
    content: " ";
    top: 20px;
    left: 0;
}
#menu-toggle:checked + #trigger + #burger {
    top: 35px;
    transform: rotate(180deg);
    transition: transform .2s ease;
}

#menu-toggle:checked + #trigger + #burger:before {
    width: 20px;
    top: -2px;
    left: 18px;
    transform: rotate(45deg) translateX(-5px);
    transition: transform .2s ease;
}
#menu-toggle:checked + #trigger + #burger:after {
    width: 20px;
    top: 2px;
    left: 18px;
    transform: rotate(-45deg) translateX(-5px);
    transition: transform .2s ease;
}
#menu {
    position: absolute;
    top: 0; /* Align the top edge */
    left: 0; /* Align the right edge */
    margin: 0; 
    padding: 0;
    width: 110px;
    height: 110px;
    background-color: rgb(64, 151, 249);
    border-bottom-right-radius: 100%;
    box-shadow: 0 2px 5px rgba(0,0,0,0.26);
    animation: not-checked-anim .2s both;
    transition: .2s;
    overflow: hidden; /* Dodajte overflow hidden */
}

#menu-toggle:checked + #trigger + #burger + #menu {
    animation: checked-anim 1s ease both;
}
#menu-toggle:checked + #trigger ~ #menu > li, a {
    display: block;
}
[type="checkbox"]:not(:checked), [type="checkbox"]:checked {
    display: none;
}
.factories-list-container {
  max-height: 600px; /* Prilagodi visinu po potrebi */
  overflow-y: auto;
}
</style>
