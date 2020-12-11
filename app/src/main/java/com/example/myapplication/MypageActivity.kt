package com.example.myapplication

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.ColorTemplate.COLORFUL_COLORS
import kotlinx.android.synthetic.main.activity_mypage.*
import me.itangqi.waveloadingview.WaveLoadingView


class MypageActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mypage)

        //test를 위한 seekbar
        var seekbar: SeekBar = findViewById(R.id.seekbar)  //java 에서는 SeekBar seekBar = (SeekBar)findViewById(R.id.seekbar)

        //남은 시간을 나타내는 원 웨이브 이펙트
        var waveLoadongView: WaveLoadingView = findViewById(R.id.waveLoadongView)
        waveLoadongView.setProgressValue(0);

        //seekbar의 동작에 따라 달라지는 웨이브 이팩트 progress가 시간(현재까지 소요된 시간)이고 50분이 max로 설정해둠 남은시간(50-progress)을 표시하도록 설정해둠
        seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                waveLoadongView.setProgressValue(progress);

                if (progress < 90) {
                    waveLoadongView.setBottomTitle("");
                    waveLoadongView.setCenterTitle(String.format("%d분", (100 - progress) / 2));
                    waveLoadongView.setTopTitle("");
                    waveLoadongView.setWaveColor(Color.parseColor("#8ECAE6"));
                } else {
                    waveLoadongView.setBottomTitle("");
                    waveLoadongView.setCenterTitle(String.format("%d분", (100 - progress) / 2));
                    waveLoadongView.setTopTitle("");
                    waveLoadongView.setWaveColor(Color.parseColor("#FFB703"));
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }

        })
        var user_num = intent.getStringExtra("user_num")
        var id = intent.getStringExtra("id")
        var pw = intent.getStringExtra("pw")
        var name = intent.getStringExtra("name")
        var dorm_num = intent.getStringExtra("dorm_num")
        var phone_num = intent.getStringExtra("phone_num")
        var using_num = intent.getStringExtra("using_num")

        mypage_name.text = "이름 : " + name
        mypage_id.text = "ID : " + id
        mypage_phone.text = "연락처 : " + phone_num
        mypage_dorm.text = "기숙사 : " + dorm_num

        /*사용자 데이터 분석 그래프*/

        //요일 원형 그래프
        /*수정 필요 : 데이터 data_all, data_my 배열에 INT형으로 넣으면 됨! */
        var data_all = arrayOf(10, 20, 30, 40, 50, 50, 60)    //예시
        var data_my = arrayOf(500, 300, 750, 800, 400, 700, 200)  //예시
        makedaycharts(data_all, data_my)


        //시간 꺾은 선 그래프
        /*아직 미완 */
        var labelList = arrayOf(11.2.toFloat(), 1.4.toFloat())  //testing
        var jsonList = arrayOf(132.toFloat(), 404.toFloat())  //testing
        maketimecharts(labelList, jsonList)

        /*스위치 버튼 이벤트*/
        switchreport.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener
        { buttonView, isChecked ->
            if (isChecked == true) {
                //Toast.makeText(this, "스위치-ON", Toast.LENGTH_SHORT).show()
                chart.setEnabled(true)
                chart.visibility = View.INVISIBLE
                chart2.setEnabled(true)
                chart2.visibility = View.INVISIBLE
                lineChart.visibility = View.VISIBLE
            } else {
                //Toast.makeText(this, "스위치-OFF", Toast.LENGTH_SHORT).show()
                chart.setEnabled(false)
                chart.visibility = View.VISIBLE
                chart.animateY(1400, Easing.EaseInOutQuad)
                chart.setEnabled(false)
                chart2.visibility = View.VISIBLE
                chart2.animateY(1400, Easing.EaseInOutQuad)
                lineChart.visibility = View.INVISIBLE
            }
        })
    }

    fun makedaycharts(data_all: Array<Int>, data_my: Array<Int>) :Unit{
        /* 요일 원형 그래프 */
        chart.setUsePercentValues(true)
        chart2.setUsePercentValues(true)

        // 데이터 set
        val chart1_entries = ArrayList<PieEntry>()
        chart1_entries.add(PieEntry(data_all[0].toFloat(), "월"))
        chart1_entries.add(PieEntry(data_all[1].toFloat(), "화"))
        chart1_entries.add(PieEntry(data_all[2].toFloat(), "수"))
        chart1_entries.add(PieEntry(data_all[3].toFloat(), "목"))
        chart1_entries.add(PieEntry(data_all[4].toFloat(), "금"))
        chart1_entries.add(PieEntry(data_all[5].toFloat(), "토"))
        chart1_entries.add(PieEntry(data_all[6].toFloat(), "일"))
        val chart2_entries = ArrayList<PieEntry>()
        chart2_entries.add(PieEntry(data_my[0].toFloat(), "월"))
        chart2_entries.add(PieEntry(data_my[1].toFloat(), "화"))
        chart2_entries.add(PieEntry(data_my[2].toFloat(), "수"))
        chart2_entries.add(PieEntry(data_my[3].toFloat(), "목"))
        chart2_entries.add(PieEntry(data_my[4].toFloat(), "금"))
        chart2_entries.add(PieEntry(data_my[5].toFloat(), "토"))
        chart2_entries.add(PieEntry(data_my[6].toFloat(), "일"))

        // 각 블록 색깔 지정 (꼭 리스트 값안에 담아야 함)
        val colorsItems = ArrayList<Int>()
        //for (c in ColorTemplate.VORDIPLOM_COLORS) colorsItems.add(c)
        colorsItems.add(Color.parseColor("#FF9AA2"))
        colorsItems.add(Color.parseColor("#FFB7B2"))
        colorsItems.add(Color.parseColor("#FFDAC1"))
        colorsItems.add(Color.parseColor("#E2F0CB"))
        colorsItems.add(Color.parseColor("#B5EAD7"))
        colorsItems.add(Color.parseColor("#C7CEEA"))
        colorsItems.add(Color.parseColor("#B7BEFF"))

        //PieDataSet 변수를 만들어 위에서 셋팅한 색상과 그래프에 들어갈 퍼센테이지 수치 색상과 사이즈를 지정할 수 있다.
        //생성할 때, entries는 위에서 데이터셋한 리스트의 이름이고 오른쪽은 value값인데 빈값으로 셋팅해도 된다
        val chart1_pieDataSet = PieDataSet(chart1_entries, "")
        chart1_pieDataSet.apply {
            colors = colorsItems
            valueTextColor = Color.BLACK
            valueTextSize = 16f
        }
        val chart2_pieDataSet = PieDataSet(chart2_entries, "")
        chart2_pieDataSet.apply {
            colors = colorsItems
            valueTextColor = Color.BLACK
            valueTextSize = 16f
        }

        //PieData변수에 위에서 만든 PieDataSet을 넘겨주고, xml에서 만든 chart에 데이터를 셋팅해주는 부분
        val pieData = PieData(chart1_pieDataSet)
        chart.apply {
            data = pieData
            //그래프 이름...보기싫어서 안보이게 해둠..^^
            description.isEnabled = false
            //그래프 만지면 회전하는 애니메이션.. 안쓸꺼임^^
            isRotationEnabled = false
            centerText = "MY"
            setCenterTextSize(20f)
            setEntryLabelColor(Color.BLACK)
            setEntryLabelTextSize(10f)
            //그래프 최초 시작시 12시방향으로 휘리릭.. 이뿌다....^^
            animateY(1400, Easing.EaseInOutQuad)
            animate()
        }
        val pieData2 = PieData(chart2_pieDataSet)
        chart2.apply {
            data = pieData2
            description.isEnabled = false
            isRotationEnabled = false
            centerText = "ALL"
            setCenterTextSize(20f)
            setEntryLabelColor(Color.BLACK)
            setEntryLabelTextSize(10f)
            animateY(1400, Easing.EaseInOutQuad)
            animate()

        }

        //아래 라벨 숨기기
        val l: Legend = chart.getLegend()
        l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        l.orientation = Legend.LegendOrientation.VERTICAL
        l.setDrawInside(false)
        l.isEnabled = false

        val l2: Legend = chart2.getLegend()
        l2.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        l2.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        l2.orientation = Legend.LegendOrientation.VERTICAL
        l2.setDrawInside(false)
        l2.isEnabled = false
    }

    fun maketimecharts(labelList : Array<Float> , valList : Array<Float> ) :Unit{
        //Part1
        val entries = ArrayList<Entry>()
        val entries2 = ArrayList<Entry>()

        //Part2
        entries.add(Entry(1f, 10f))
        entries.add(Entry(2f, 2f))
        entries.add(Entry(3f, 7f))
        entries.add(Entry(4f, 20f))
        entries.add(Entry(5f, 16f))
        entries.add(Entry(6f, 1f))
        entries.add(Entry(7f, 2f))
        entries.add(Entry(8f, 4f))
        entries.add(Entry(9f, 10f))
        entries.add(Entry(10f, 18f))
        entries.add(Entry(11f, 18f))
        entries.add(Entry(12f, 18f))


        entries2.add(Entry(1f, 12f))
        entries2.add(Entry(2f, 11f))
        entries2.add(Entry(3f, 2f))
        entries2.add(Entry(4f, 0f))
        entries2.add(Entry(5f, 0f))
        entries2.add(Entry(6f, 10f))
        entries2.add(Entry(7f, 3f))
        entries2.add(Entry(8f, 8f))
        entries2.add(Entry(9f, 5f))
        entries2.add(Entry(10f, 0f))
        entries2.add(Entry(11f, 2f))
        entries2.add(Entry(12f, 1f))

        //Part3
        val vl = LineDataSet(entries, "All")
        val v2 = LineDataSet(entries2, "My")

        //Part4
        vl.setDrawValues(false)
        vl.setDrawFilled(true)
        vl.setColor(R.color.design_default_color_error)
        vl.lineWidth = 3f
        vl.fillColor = R.color.lightorange
        vl.fillAlpha = R.color.deeporange

        v2.setDrawValues(false)
        v2.setDrawFilled(false)
        v2.lineWidth = 3f
        v2.fillColor = R.color.design_default_color_error
        v2.fillAlpha = R.color.design_default_color_secondary


        //Part5
        lineChart.xAxis.labelRotationAngle = 0f

        //Part6
        lineChart.data = LineData(vl)
        lineChart.data = LineData(v2)

        val chartData = LineData()
        chartData.addDataSet(vl)
        chartData.addDataSet(v2)
        lineChart.data = chartData

        lineChart.invalidate()

        //Part7
        lineChart.axisRight.isEnabled = false


        //Part8
        lineChart.setTouchEnabled(true)
        lineChart.setPinchZoom(true)

        //Part9
        lineChart.description.text = "Parcent"
        lineChart.setNoDataText("아직 데이터가 부족합니다!")

        //Part10
        lineChart.animateX(1800, Easing.EaseInExpo)

        lineChart.visibility=View.INVISIBLE
    }
}

