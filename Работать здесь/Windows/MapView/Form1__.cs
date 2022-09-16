using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
//using System.Linq;
using System.Text;
using System.Windows.Forms;
using System.Net;
using System.IO;
using System.Drawing.Drawing2D;
using System.Collections;
using System.Security.AccessControl;
using System.Security.Permissions;
using System.Threading;



namespace WindowsFormsApplication1
{
    public partial class Form1 : Form
    {
        public struct ZoomLevel{
            public bool Activated;
            public int TileCnt;
            public PointF TopLeft;
            public PointF BotRite;
        }

        ZoomLevel[] Level = new ZoomLevel[18];

        bool BackgroundworkerCancel;
        
        /// <summary>
        /// constructor
        /// </summary>
        public Form1()
        {
            InitializeComponent();

        }




        public PointF WorldToTilePos(double lon, double lat, int zoom)
        {
            PointF p = new Point();
            p.X = (float)(((lon + 180.0) / 360.0) * (1 << zoom));
            p.Y = (float)((1.0 - Math.Log(Math.Tan(lat * Math.PI / 180.0) +
                1.0 / Math.Cos(lat * Math.PI / 180.0)) / Math.PI) / 2.0 * (1 << zoom));

            return p;
        }

        public PointF TileToWorldPos(double tile_x, double tile_y, int zoom)
        {
            PointF p = new Point();
            double n = Math.PI - ((2.0 * Math.PI * tile_y) / Math.Pow(2.0, zoom));

            p.X = (float)((tile_x / Math.Pow(2.0, zoom) * 360.0) - 180.0);
            p.Y = (float)(180.0 / Math.PI * Math.Atan(0.5 * (Math.Exp(n) - Math.Exp(-n))));

            return p;
        }
        /// <summary>
        /// Load tile from webserver
        /// </summary>
        /// <param name="urltile"></param>
        public void GetTile(string urltile)
        {
            HttpWebRequest wreq;
            HttpWebResponse wresp;
            Stream mystream;
            Bitmap bmp;
            Uri tileurl;

            bmp = null;
            mystream = null;
            wresp = null;
            try
            {
                tileurl = new Uri(urltile);


                wreq = (HttpWebRequest)WebRequest.Create(tileurl);
                wreq.AllowWriteStreamBuffering = true;

                wresp = (HttpWebResponse)wreq.GetResponse();

                if ((mystream = wresp.GetResponseStream()) != null)
                {
                    //toolStripStatusLabel1.Text = ("Image loaded!");
                    bmp = new Bitmap(mystream);
                }
            }
            catch (Exception e)
            {
                MessageBox.Show(e.Message);
            }
            finally
            {
                if (mystream != null)
                    mystream.Close();

                if (wresp != null)
                    wresp.Close();

                pictureBox1.Image = bmp;
            }
        }


        /// <summary>
        /// Calculate button clicked
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void button3_Click(object sender, EventArgs e)
        {
            PointF punkt = new Point();
            string url;
            //calc xy tile coord of currently specified world coords
            punkt = WorldToTilePos(Double.Parse(Lat.Text), Double.Parse(Lon.Text), int.Parse(zoom.Text));
            //rounded int values
            tilex.Text = Convert.ToString(Math.Floor(punkt.X));
            tiley.Text = Convert.ToString(Math.Floor(punkt.Y));
            //create url for this tile 
            url = tileserver.Text + zoom.Text + "/" + tilex.Text + "/" + tiley.Text +".png";

            toolStripStatusLabel1.Text = (url);
            button4.Enabled = true;
            button7.Enabled = true;
        }
        /// <summary>
        /// Load button pressed
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void button4_Click(object sender, EventArgs e)
        {
            GetTile(tileserver.Text + zoom.Text + "/" + tilex.Text + "/" + tiley.Text + ".png");
        }
        /// <summary>
        /// Convert image from picture box to binary format
        /// </summary>
        /// <param name="p1"></param>
        /// <param name="file"></param>
        /// <returns></returns>
        private bool ConvertImage(PictureBox p1, String file)
        {
            string filepath;
            try
            {
                Bitmap b = new Bitmap(p1.Image);

                short pixel;

             
                filepath = file.Substring(0, file.LastIndexOf("\\"));
                if (!Directory.Exists(@filepath))
                {
                    Directory.CreateDirectory(@filepath);
                    AddDirectorySecurity(@filepath, "Administrator", FileSystemRights.FullControl,
                                        InheritanceFlags.ObjectInherit | InheritanceFlags.ContainerInherit,
                                        PropagationFlags.None, AccessControlType.Allow);

                }

                pictureBox1.Image.Save(file, System.Drawing.Imaging.ImageFormat.Jpeg);

                //FileStream writeStream = new FileStream(file, FileMode.Create);
                //using (BinaryWriter bw = new BinaryWriter(writeStream))
                //{
                //    // write header
                //    bw.Write((byte)p1.Height);
                //    bw.Write((byte)p1.Width);
                    
                //    // future use
                //    bw.Write((short)0);
                //    bw.Write((short)0);
                //    bw.Write((short)0);

                //    for (int j = 0; j < p1.Width; j++)
                //    {
                //        for (int i = p1.Height - 1; i >= 0; i--)
                //        {
                //            Color c = b.GetPixel(j, i);
                //            pixel = (short)(((c.G & 0x07) << 13) + ((c.G) >> 5) + (((c.B) >> 3) << 8) + (((c.R) >> 3) << 3));  //algorithm thanks to sjoerd
                //            bw.Write(pixel);
                //        }
                //    }
                //}
            }
            catch (Exception e)
            {
                MessageBox.Show(e.Message);
                return false;
            }
            return true;
        }



        private void backgroundWorker1_DoWork(object sender, DoWorkEventArgs e)
        {
            PointF topleft = new Point();
            PointF botrite = new Point();

            int i;
            string s;

            double percentage;
            //Control.CheckForIllegalCrossThreadCalls = false;
            
            //load all tiles from tileserver
            for (i = 0; i < listBox1.Items.Count; i++)
            {
                s = listBox1.Items[i].ToString();//get tile http address
                GetTile(s); //get tile from server
                s = s.Replace(e.Argument.ToString(), ""); //remove server name
                //s = s.Replace(".png",".prm"); //exchange file extension - i don't exchange this

                s = outputfolder.Text + "/" + s; //add output folder

                s = s.Replace("/", "\\"); //replace slashes
                //i don't need to convert to any binary format
                ConvertImage(pictureBox1, s); //convert to binary format
                //i need save file as picture 
                //pictureBox1.Image.Save(s, System.Drawing.Imaging.ImageFormat.Png);


                //calculate progress bar
                percentage = ((i+1)* 100.0 / (double)listBox1.Items.Count);

                backgroundWorker1.ReportProgress((int)percentage );

                if (backgroundWorker1.CancellationPending)
                    break;
            }
            
        }

        private void backgroundWorker1_ProgressChanged(object sender, ProgressChangedEventArgs e)
        {
            this.toolStripProgressBar1.Value = e.ProgressPercentage;

        }

        /// <summary>
        /// Save tile file
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void button7_Click(object sender, EventArgs e)
        {
            saveFileDialog1.ShowDialog();

            if(saveFileDialog1.FileName != "")
                pictureBox1.Image.Save(saveFileDialog1.FileName, System.Drawing.Imaging.ImageFormat.Jpeg);
        }

        private void multiple_calculate_Click(object sender, EventArgs e)
        {
            PointF topleft = new Point();
            PointF botrite = new Point();
 
            int i, x, y;
            string s;

            listBox1.Items.Clear();//hidden listbox for downloaded tiles
            toolStripProgressBar1.Value = 0;
            BackgroundworkerCancel = false;

            for (i = 0; i <= (checkedListBox1.Items.Count - 1); i++)
            {
                //for (j = 0; j <= (Level[i].TileCnt); j++)
                {
                    if (Level[i].Activated)
                    {
                        for (x = (int)Level[i].TopLeft.X; x < (int)Level[i].BotRite.X; x++)
                        {
                            for (y = (int)Level[i].BotRite.Y; y < (int)Level[i].TopLeft.Y; y++)
                            {
                                s = tileserver.Text  + Convert.ToString(i) + "/" + Convert.ToString(x) +
                                    "/" + Convert.ToString(y) + ".png";
                                listBox1.Items.Add(s);//add tile address to list
                            }
                        }
                        
                        backgroundWorker1.RunWorkerAsync(tileserver2.Text);

                        while (backgroundWorker1.IsBusy)
                        {
                            Thread.Sleep(100);
                            Application.DoEvents(); 
                        }
                        toolStripStatusLabel1.Text = "Tiles zoomlevel " + Convert.ToString(i) + " downloaded!";
                    }
                    if (BackgroundworkerCancel)
                        break;
                }
            }
        }


        private void checkedListBox1_SelectedIndexChanged(object sender, EventArgs e)
        {
            PointF topleft = new Point();
            PointF botrite = new Point();
            double tilecntx, tilecnty;

            int i, cnt = 0, cnttotal = 0 ;
            //walk over all level's
            for (i = 0; i <= (checkedListBox1.Items.Count - 1); i++)
            {
                if (checkedListBox1.GetItemChecked(i))
                {
                    Level[i].Activated = true;
                    //setup points by coordinates
                    topleft = WorldToTilePos(Double.Parse(startlat.Text), Double.Parse(startlon.Text), i);
                    botrite = WorldToTilePos(Double.Parse(endlat.Text), Double.Parse(endlon.Text), i);
                    //save points
                    Level[i].TopLeft = topleft;
                    Level[i].BotRite = botrite;
                    //calc tile id's
                    tilecntx = Math.Abs(Math.Floor(botrite.X) - Math.Floor(topleft.X));
                    tilecnty = Math.Abs(Math.Floor(botrite.Y) - Math.Floor(topleft.Y));
                    //calc overall tiles
                    cnt = (int)(tilecntx * tilecnty);
                    cnttotal += cnt;
                    Level[i].TileCnt = cnt;
                }
                else
                {
                    Level[i].Activated = false;
                }

            }
            tiles2download.Text = Convert.ToString(cnttotal);


        }
        
        //setup output folder
        private void button2_Click(object sender, EventArgs e)
        {
            folderBrowserDialog1.ShowDialog();

            if (folderBrowserDialog1.SelectedPath != "")
            {
                outputfolder.Text = folderBrowserDialog1.SelectedPath;
            }
        }

        /// <summary>
        /// Show coord of current tile to user
        /// </summary>
        private void showCurrentTileCoords()
        {
            //get tile ID's
            int x = Convert.ToInt32(tilex.Text);
            int y = Convert.ToInt32(tiley.Text);
            int z = Convert.ToInt32(zoom.Text);
            // calc tile coords (it is center or topleft)?
            PointF crd = TileToWorldPos(x, y, z);
            //show this to user
            Lat.Text = crd.X.ToString();
            Lon.Text = crd.Y.ToString();

        }

        private void up_Click(object sender, EventArgs e)
        {
            if (tiley.Text != "0")
            {
                tiley.Text = Convert.ToString(Convert.ToInt32(tiley.Text) - 1);
                showCurrentTileCoords();
                GetTile(tileserver.Text + zoom.Text + "/" + tilex.Text + "/" + tiley.Text + ".png");
            }
        }


        private void down_Click(object sender, EventArgs e)
        {
            if (tiley.Text != "0")
            {
                tiley.Text = Convert.ToString(Convert.ToInt32(tiley.Text) + 1);
                showCurrentTileCoords();
                GetTile(tileserver.Text + zoom.Text + "/" + tilex.Text + "/" + tiley.Text + ".png");
            }
        }

        private void left_Click(object sender, EventArgs e)
        {
            if (tilex.Text != "0")
            {
                tilex.Text = Convert.ToString(Convert.ToInt32(tilex.Text) - 1);
                showCurrentTileCoords();
                GetTile(tileserver.Text + zoom.Text + "/" + tilex.Text + "/" + tiley.Text + ".png");
            }
        }

        private void right_Click(object sender, EventArgs e)
        {
            if (tilex.Text != "0")
            {
                tilex.Text = Convert.ToString(Convert.ToInt32(tilex.Text) + 1);
                showCurrentTileCoords();
                GetTile(tileserver.Text + zoom.Text + "/" + tilex.Text + "/" + tiley.Text + ".png");
            }
        }


        private void listBox1_SelectedIndexChanged(object sender, EventArgs e)
        {
            string s;

            s = listBox1.Items[listBox1.SelectedIndex].ToString();

            MessageBox.Show(s.Replace(tileserver.Text,""));
        }

        // Adds an ACL entry on the specified directory for the specified account. 
        public static void AddDirectorySecurity(string FileName, string Account, FileSystemRights Rights,
                                                InheritanceFlags Inheritance, PropagationFlags Propogation,
                                                AccessControlType ControlType)
        {
            // Create a new DirectoryInfo object. 
            DirectoryInfo dInfo = new DirectoryInfo(FileName);
            // Get a DirectorySecurity object that represents the  
            // current security settings. 
            DirectorySecurity dSecurity = dInfo.GetAccessControl();
            // Add the FileSystemAccessRule to the security settings.  
            dSecurity.AddAccessRule(new FileSystemAccessRule(Account,
                                                             Rights,
                                                             Inheritance,
                                                             Propogation,
                                                             ControlType));
            // Set the new access settings. 
            dInfo.SetAccessControl(dSecurity);
        }

        private void backgroundWorker1_RunWorkerCompleted(object sender, RunWorkerCompletedEventArgs e)
        {
            //MessageBox.Show("All files downloaded!");
            
           
        }

        private void CancelDownload_Click(object sender, EventArgs e)
        {
            backgroundWorker1.CancelAsync();
            BackgroundworkerCancel = true;

            toolStripStatusLabel1.Text = "Download canceled!";
            toolStripProgressBar1.Value = 0;
        }

        private void startlat_TextChanged(object sender, EventArgs e)
        {
            if (startlat.Text == "" || startlon.Text == "" || endlat.Text == "" || endlon.Text == "")
                multiple_calculate.Enabled = false;
            else
                multiple_calculate.Enabled = true;
        }

        private void endlat_TextChanged(object sender, EventArgs e)
        {
            if (startlat.Text == "" || startlon.Text == "" || endlat.Text == "" || endlon.Text == "")
                multiple_calculate.Enabled = false;
            else
                multiple_calculate.Enabled = true;
        }

        private void startlon_TextChanged(object sender, EventArgs e)
        {
            if (startlat.Text == "" || startlon.Text == "" || endlat.Text == "" || endlon.Text == "")
                multiple_calculate.Enabled = false;
            else
                multiple_calculate.Enabled = true;
        }

        private void endlon_TextChanged(object sender, EventArgs e)
        {
            if (startlat.Text == "" || startlon.Text == "" || endlat.Text == "" || endlon.Text == "")
                multiple_calculate.Enabled = false;
            else
                multiple_calculate.Enabled = true;
        }

        /// <summary>
        /// My test button determine size of tile for each zoom
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void button1_test_Click(object sender, EventArgs e)
        {
            System.IO.StreamWriter sw = new StreamWriter("coords.txt");
            sw.WriteLine("tile size: zoom; X; Y;");
            for (int i = 1; i < 17; i++)
            {
                PointF pp = WorldToTilePos(55.0, 55.0, i);
                PointF f1 = TileToWorldPos(pp.X, pp.Y, i);
                PointF f2 = TileToWorldPos(pp.X + 1, pp.Y + 1, i);
                double x = (f2.X - f1.X);
                double y = (f2.Y - f1.Y);
                sw.WriteLine("{0}; {1}; {2};", i, x, y);
            }
            sw.Close();
        }



    }
}

