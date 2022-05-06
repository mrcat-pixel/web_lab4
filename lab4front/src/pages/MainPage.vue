<template>
  <div class="container">
    <div class="control-panel">
      <button type="button" class="btn reset-colors material-icons" v-on:click="logout">logout</button>
      <div @mousemove="graphMouseMove" @mousedown="send" class="center-text">
        <svg height="300" width="300" xmlns="http://www.w3.org/2000/svg" id="graph" class="shadow-graph" style="pointer-events: none">
          <polygon fill="palevioletred" fill-opacity="0.3" points="230,150 150,150 150,110" id="figure1"/>

          <polygon fill="mediumspringgreen" fill-opacity="0.3" points="70,150 70,190 150,190 150,150" id="figure2"/>

          <path d="M 70 150 L 150 150 L 150 70 C 110 70 70 110 70 150 Z" fill="rgb(174, 193, 187)" fill-opacity="0.3" id="figure3"/>

          <line stroke="rgb(174, 193, 187)" x1="10" x2="290" y1="150" y2="150" stroke-width="5" stroke-linecap="round"/>
          <line stroke="rgb(174, 193, 187)" x1="150" x2="150" y1="10" y2="290" stroke-width="5" stroke-linecap="round"/>
          <polygon fill="rgb(174, 193, 187)" points="150,0 140,15 160,15"/>
          <polygon fill="rgb(174, 193, 187)" points="300,150 285,160 285,140"/>
          <g v-for="point in points" :key="point.id">
            <circle v-bind:cx="point.x * 20 + 150" v-bind:cy="(-point.y) * 20 + 150" r="4" class="littleDot"
                    v-bind:fill="point.insideArea ? 'mediumspringgreen' : 'palevioletred'"></circle>
          </g>
        </svg>
      </div>
      <div class="X_select">
        <p class="prompt-text">Введите X (-4 ... 4):</p>
        <div class="box-visual invert-colors">
          <div v-for="i in 9" :key="i">
            <input type="radio" name="x" :id="'x_' + (i - 5).toString().replace('-', '_')" :value="i - 5" v-model="xValue">
            <label :for="'x_' + (i - 5).toString().replace('-', '_')">{{ i - 5 }}</label>
          </div>
        </div>
      </div>
      <div class="Y_select">
        <p class="prompt-text">Введите Y (-5 ... 3):</p>
        <div class="center-text">
        <input type="text" class="text-input" placeholder="Введите значение..." v-model="yValue" >
        </div>
      </div>
      <div class="R_select">
        <p class="prompt-text">Введите R (0 ... 4):</p>
        <div class="box-visual invert-colors">
          <div v-for="i in 9" :key="i">
            <input type="radio" name="r" :id="'r_' + (i - 5).toString().replace('-', '_')" :value="i - 5"
                   v-model="rValue" v-on:input="redrawFigure">
            <label :for="'r_' + (i - 5).toString().replace('-', '_')">{{ i - 5 }}</label>
          </div>
        </div>
      </div>
      <br/>
      <button class="btn confirm-colors material-icons" v-on:click="send">send</button>
      <button class="btn reset-colors material-icons" v-on:click="resetWithClear">backspace</button>
    </div>
    <div class="indent-box">
      <table class="result-table" id="result">
        <thead>
        <tr>
          <td>X</td>
          <td>Y</td>
          <td>R</td>
          <td>?</td>
          <td>Время</td>
          <td>Вып-е</td>
        </tr>
        </thead>

        <tbody v-if="points">
        <tr v-for="point in points" :key="point.id">
          <td>{{point.x}}</td>
          <td>{{point.y.toFixed(2)}}</td>
          <td>{{point.r}}</td>
          <td>{{point.insideArea ? 'да' : 'нет'}}</td>
          <td>{{point.timestamp}}</td>
          <td>{{point.executionTime + 'мс'}}</td>
        </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script>


export default {
  name: "MainPage",
  data() {
    return {
      xValue: 0,
      yValue: 0,
      rValue: 4,
      points: undefined
    }
  },

  created: async function() {

    let res = await fetch('https://wf.savok.pw/lab4back-1_0-SNAPSHOT/api/user', {
      credentials: 'include'
    });

    if (!res.ok) await this.$router.push('/')
    else {
      res = await fetch('https://wf.savok.pw/lab4back-1_0-SNAPSHOT/api/points', {
        credentials: 'include'
      });

      if (res.ok) {
        this.points = await res.json();
      } else await this.$router.push('/')
    }
  },

  methods: {
    send: async function() {
      if (this.xValue === "") {
        alert('Неверное значение X')
        return;
        // TODO: maybe we should show this error message at the page?
      }

      const trueY = +this.yValue;
      if (this.yValue === "" || isNaN(trueY) || trueY < -5 || trueY > 3) {
        alert('Неверное значение Y')
        return;
        // TODO: maybe we should show this error message at the page?
      }

      if (this.rValue === "" || +this.rValue < 0) {
        alert('Неверное значение R')
        return;
        // TODO: maybe we should show this error message at the page?
      }

      const res = await fetch('https://wf.savok.pw/lab4back-1_0-SNAPSHOT/api/points', {
        method: 'POST',
        body: new URLSearchParams({
          x: this.xValue,
          y: this.yValue,
          r: this.rValue
        }),
        credentials: 'include'
      })
      if (!res.ok) {
        await this.$router.push("/")
      } else {
        const json = await res.json();
        this.points.push(json);
      }
    },
    reset: function() {
      this.xValue = 0;
      this.yValue = 0;
      this.rValue = 4;
    },
    resetWithClear: async function() {
      this.reset()

      await fetch('https://wf.savok.pw/lab4back-1_0-SNAPSHOT/api/points', {
        method: 'DELETE',
        credentials: 'include'
      })

      this.points = []
    },
    logout: async function() {
      await fetch('https://wf.savok.pw/lab4back-1_0-SNAPSHOT/api/user', {
        credentials: 'include',
        method: "DELETE"
      });
      document.cookie = "";
      await this.$router.push("/")
    },

    getCoords(elem) {
      let box = elem.getBoundingClientRect();

      return {
        top: box.top + window.pageYOffset,
        right: box.right + window.pageXOffset,
        bottom: box.bottom + window.pageYOffset,
        left: box.left + window.pageXOffset
      };
    },

    drawOMarker(x, y) {
      this.deleteOMarker();
      const svg = document.getElementById('graph');

      let circle = document.createElementNS("http://www.w3.org/2000/svg", 'circle');
      circle.setAttributeNS(null, 'cx', (x * 20 + 150).toString());
      circle.setAttributeNS(null, 'cy', (-y * 20 + 150).toString());
      circle.setAttributeNS(null, 'r', '8');
      circle.setAttributeNS(null, 'stroke', 'rgb(174, 193, 187)');
      circle.setAttributeNS(null, 'stroke-width', '5');
      circle.setAttributeNS(null, 'fill-opacity', '0');
      circle.id = "selected_pos";
      svg.appendChild(circle);
    },

    deleteOMarker() {
      let circle = document.getElementById('selected_pos');
      if (circle !== null) circle.parentElement.removeChild(circle);
    },

    graphMouseMove: function (evt) {
      let offset = this.getCoords(evt.target);
      const width = offset.right - offset.left;
      const height = offset.bottom - offset.top;

      let x = Math.round((evt.pageX - offset.left - width / 2) / (width / 2) * (5 / 2 * 3));
      let y = -Number(((evt.pageY - offset.top - height / 2) / (height / 2) * (5 / 2 * 3)).toFixed(3));

      if (x < -4) { x = -4; }
      else if (x > 4) { x = 4; }

      if (y < -5 ) { y = -5; }
      else if ( y > 3) { y = 3; }

      this.drawOMarker(x, y);

      this.xValue = x;
      this.yValue = y;
    },

    redrawFigure: function() {
      setTimeout(() => this.executeRedraw(this.rValue), 100);
    },

    executeRedraw(scale){
      let figure1 = document.getElementById("figure1");
      if (scale < 0) scale = 0;

      figure1.setAttributeNS(null, 'points',
          (150 + 20*scale) + ",150 150,150 150," + (150 - 10*scale)
      );

      let figure2 = document.getElementById("figure2");
      figure2.setAttributeNS(null, 'points',
          (150 - 20*scale) + ",150 " + (150 - 20*scale) + "," + (150 + 10*scale) + " 150," + (150 + 10*scale) + " 150,150"
      );

      let figure3 = document.getElementById("figure3");
      figure3.setAttributeNS(null, 'd',
          "M " + (150 - 20*scale) + " 150 L 150 150 L 150 " + (150 - 20*scale) + " C " + (150 - 10*scale) + " " +
          (150 - 20*scale) + " " + (150 - 20*scale) + " " + (150 - 10*scale) + " " + (150 - 20*scale) + " 150 Z"
      );
    }
  }
}
</script>