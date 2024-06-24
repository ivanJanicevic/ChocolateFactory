<template>
  <div class="center-position">
    <form class="formStyle" style="margin-top:5%;">
      <fieldset>
        <legend>Log in</legend>
        <div>
          <label for="username" class="formInputs">Username</label><br>
          <input type="text" v-model="userCredentials.username" name="username" id="username" class="formInputs">
        </div>
        <div>
          <label for="password" class="formInputs">Password</label><br>
          <input type="password" v-model="userCredentials.password" name="password" id="password" class="formInputs">
        </div><br>
        <div>
          <input type="submit" v-on:click="loginUser" value="Login" class="formInputs">
        </div>
        <p>{{errortext}}</p>
      </fieldset><br>
      <div>
        <input type="submit" v-on:click="registerUser" value="Register" class="formInputs">
      </div><br>
      <div>
        <input type="submit" v-on:click="goBack" value="Return" class="formInputs">
      </div>
    </form>
  </div>
</template>

<script>
import axios from 'axios';
import router from '@/router';

export default {
  data() {
    return {
      user: {
        id: ' ',
        username: null,
        password: null,
        name: null,
        surname: null,
        gender: null,
        birthDate: null,
        role: null,
      },
      userCredentials: {
        username: null,
        password: null
      },
      isUsernameValid: null,
      isPasswordValid: null,
      errortext: ''
    };
  },
  methods: {
    loginUser(event) {
      event.preventDefault();
      if (!this.userCredentials.username) {
        document.getElementsByName("username")[0].style.background = "red";
        this.isUsernameValid = false;
        this.errortext = 'All fields are required';
      } else {
        document.getElementsByName("username")[0].style.background = "white";
        this.isUsernameValid = true;
        this.errortext = '';
      }
      if (!this.userCredentials.password) {
        document.getElementsByName("password")[0].style.background = "red";
        this.isPasswordValid = false;
        this.errortext = 'All fields are required';
      } else {
        document.getElementsByName("password")[0].style.background = "white";
        this.isPasswordValid = true;
        this.errortext = '';
      }
      if (this.isUsernameValid && this.isPasswordValid) {
        axios.post('http://localhost:8080/WebShopAppREST/rest/user/login/', this.userCredentials)
          .then(response => {
            this.user = response.data;
            if (this.user.userStatus === 'Deactivated') {
              this.errortext = 'Your account has been deactivated.';
              return;
            }
            console.log('Uloga korisnika:');
            console.log("Uloga korisnika:" + this.user.role);
            if (this.user.role === 'Administrator') {
              router.push({ path: `/loggedInAdmin/${this.user.id}` });
            } else if (this.user.role === 'Customer') {
                this.$router.push('/factories');
            } else if (this.user.role === 'Manager') {
                this.$router.push('/factories');
            } else {
              this.errortext = 'Invalid user role';
            }

          })
          .catch(error => {
            console.error('An error occurred:', error);
            this.errortext = `User does not exist with username: "${this.userCredentials.username}"`;
          });
      }
    },
    registerUser(event) {
      event.preventDefault();
      router.push(`/register`);
    },
    goBack(event) {
      event.preventDefault();
      router.push(`/`);
    }
  }
};
</script>

<style scoped>
.center-position {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
}

.formStyle {
  width: 300px;
}

.formInputs {
  width: 100%;
  margin-bottom: 10px;
}

fieldset {
  border: 1px solid #ccc;
  padding: 10px;
}

legend {
  font-weight: bold;
}
</style>
